package org.ping_me.service.chat.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.ping_me.config.s3.S3Service;
import org.ping_me.dto.event.UserChatEvent;
import org.ping_me.dto.request.chat.message.ForwardMessageRequest;
import org.ping_me.dto.request.chat.message.ForwardMessagesRequest;
import org.ping_me.dto.request.chat.message.MarkReadRequest;
import org.ping_me.dto.request.chat.message.SendMessageRequest;
import org.ping_me.dto.request.chat.message.SendWeatherMessageRequest;
import org.ping_me.dto.response.chat.message.DeletedMessageResponse;
import org.ping_me.dto.response.chat.message.HistoryMessageResponse;
import org.ping_me.dto.response.chat.message.MessageRecalledResponse;
import org.ping_me.dto.response.chat.message.MessageResponse;
import org.ping_me.dto.response.chat.message.ReadStateResponse;
import org.ping_me.dto.response.weather.WeatherResponse;
import org.ping_me.model.User;
import org.ping_me.model.chat.DeletedMessage;
import org.ping_me.model.chat.Message;
import org.ping_me.model.chat.Room;
import org.ping_me.model.chat.RoomParticipant;
import org.ping_me.model.common.DeletedMessageId;
import org.ping_me.model.common.RoomMemberId;
import org.ping_me.model.constant.MessageType;
import org.ping_me.repository.jpa.chat.DeletedMessageRepository;
import org.ping_me.repository.jpa.chat.RoomParticipantRepository;
import org.ping_me.repository.jpa.chat.RoomRepository;
import org.ping_me.repository.mongodb.chat.MessageRepository;
import org.ping_me.service.chat.MessageCachingService;
import org.ping_me.service.chat.MessageService;
import org.ping_me.service.chat.event.message.MessageCreatedEvent;
import org.ping_me.service.chat.event.message.MessageRecalledEvent;
import org.ping_me.service.chat.event.room.RoomUpdatedEvent;
import org.ping_me.service.user.CurrentUserProvider;
import org.ping_me.service.weather.WeatherService;
import org.ping_me.utils.mapper.ChatMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Admin 8/26/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024L;

    @Value("${app.messages.cache.enabled}")
    private boolean cacheEnabled;

    // SERVICE
    private final S3Service s3Service;
    private final MessageCachingService messageCachingService;
    private final WeatherService weatherService;

    // PROVIDER
    private final CurrentUserProvider currentUserProvider;

    // REPOSITORY
    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final DeletedMessageRepository deletedMessageRepository;
    private final MessageRepository messageRepository;

    // PUBLISHER
    private final ApplicationEventPublisher eventPublisher;

    // UTILS
    private final ObjectMapper objectMapper;
    private final ChatMapper chatMapper;

    @NonFinal
    @Value("${spring.kafka.topic.user-chat-dev}")
    String userChatTopic;

    @Qualifier("kafkaObjectTemplate")
    private final KafkaTemplate<String, Object> kafkaObjectTemplate;

    /* ========================================================================== */
    /*                         CACHING MESSAGE                                    */
    /* ========================================================================== */
    // Redis List Order:
    // index 0        -> newest message
    // index increase -> older messages
    //
    // API / FE Order:
    // index 0        -> oldest message
    // index increase -> newest messages
    //
    //
    // Kkhông sửa cấu trúc này

    /* ========================================================================== */
    /*                         CÁC HÀM XỬ LÝ GỬI TIN NHẮN                         */
    /* ========================================================================== */

    // Hàm chính xử lý tin nhắn
    //
    // Quy trình:
    // 1. Lấy thông tin người dùng hiện tại
    // 2. Kiểm tra phòng chat và quyền tham gia
    // 3. Kiểm tra clientMsgId để tránh trùng tin nhắn
    // 4. Tạo và lưu tin nhắn mới vào cơ sở dữ liệu
    // 5. Cập nhật trạng thái phòng và người dùng
    // 6. Phát sự kiện WebSocket thông báo tin nhắn mới
    @Override
    public MessageResponse sendMessage(SendMessageRequest sendMessageRequest) {
        // Lấy thộng tin người dùng hiện tại
        var currentUser = currentUserProvider.get();

        // Trích xuất ra thông tin người gửi
        // + Mã người dùng
        // + Mã phòng chat người đã gửi
        var senderId = currentUser.getId();
        var roomId = sendMessageRequest.getRoomId();

        // Nếu file này một dạng file thì
        // validate url hợp l
        if (sendMessageRequest.getType() == MessageType.IMAGE) {
            validateImageContent(sendMessageRequest.getContent());
        } else if (sendMessageRequest.getType() == MessageType.VIDEO
                || sendMessageRequest.getType() == MessageType.FILE) {
            validateUrl(sendMessageRequest.getContent());
        }

        // Kiểm tra clientMsgId có hợp lệ không
        // clientMsgId tránh người dùng spam khi
        // đường truyền không ổn định
        UUID clientMsgId;
        try {
            clientMsgId = UUID.fromString(sendMessageRequest.getClientMsgId());
        } catch (Exception exception) {
            throw new IllegalArgumentException("clientMsg không hợp lệ");
        }

        // Tìm phòng chat mà người dùng đã gửi tin nhắn
        // Nếu tìm không được trả về lỗi
        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, senderId);
        var roomParticipant = roomParticipantRepository
                .findById(roomMemberId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên của phòng chat này"));
        validateReplyMessage(sendMessageRequest.getRepliedMessageId(), roomId);

        // Kiểm tra tin nhắn người dùng gửi đã tồn tại chưa, Kiểm tra bằng mã clientMsgId
        // Nếu tìm thấy thì trả về tin nhắn đã tồn tại trong cơ sở dữ liệu
        var existed = messageRepository
                .findByRoomIdAndSenderIdAndClientMsgId(roomId, senderId, clientMsgId)
                .orElse(null);
        if (existed != null) return chatMapper.toMessageResponseDto(existed);

        // Nếu chưa tồn tại, tạo tin nhắn mới
        Message message = new Message();
        message.setRoomId(roomId);
        message.setSenderId(senderId);
        message.setContent(sendMessageRequest.getContent());
        message.setType(sendMessageRequest.getType());
        message.setFileFormat(sendMessageRequest.getFileFormat());
        message.setRepliedMessageId(sendMessageRequest.getRepliedMessageId());
        message.setClientMsgId(clientMsgId);
        message.setCreatedAt(LocalDateTime.now());

        // Lưu tin nhắn vào cơ sở dữ liệu
        // Thực hiện Try Catch để tránh Race Condition
        // cho trường hợp User dùng 2 máy gửi tin nhắn cùng lúc
        try {
            message = messageRepository.save(message);
        } catch (DataIntegrityViolationException ex) {
            message = messageRepository
                    .findByRoomIdAndSenderIdAndClientMsgId(roomId, senderId, clientMsgId)
                    .orElseThrow(() -> ex);
        }

        try {
            // Cập nhật trạng thái phòng khi người dùng nhắn tin
            // Thông tin cập nhật bao gồm:
            // + Tin nhắn cuối cùng
            // + Thời gian nhắn tin nhắn cuối cùng
            room.setLastMessageId(message.getId());
            room.setLastMessageAt(message.getCreatedAt());

            // Cập nhật trạng thái của người dùng tham gia phòng (người dùng hiện tại)
            // Thông tin cập nhật bao gồm:
            // + Tin nhắn lần cuối đọc (seen)
            // + Thời gian đọc tin nhắn cuối cùng
            //
            // Dễ hiểu: người dùng A chat tin nhắn đó, thì mặc
            // định người dùng A đọc tất cả tin nhắn từ đầu đến tin nhắn
            // hiện tại của người dùng A
            Message finalMsg = message;
            roomParticipant.setLastReadMessageId(finalMsg.getId());
            roomParticipant.setLastReadAt(finalMsg.getCreatedAt());

            // --------------------------------------------------------------------------------
            // WEBSOCKET

            // Sự kiện MESSAGE_CREATED (tạo tin nhắn mới)
            var messageCreatedEvent = new MessageCreatedEvent(message);

            // Sử kiện ROOM_UPDATED (thông báo phòng có tin nhắn mới)
            var roomUpdatedEvent = new RoomUpdatedEvent(
                    room,
                    roomParticipantRepository.findByRoom_Id(room.getId()),
                    null
            );

            // Bắn sự kiện Websocket
            eventPublisher.publishEvent(messageCreatedEvent);
            eventPublisher.publishEvent(roomUpdatedEvent);
            // --------------------------------------------------------------------------------

            var dto = chatMapper.toMessageResponseDto(message);

            // Caching Message
            if (cacheEnabled)
                messageCachingService.cacheNewMessage(roomId, dto);

            // send chat event kafka
            publishUserChatAudit(senderId, sendMessageRequest.getContent());

            return dto;
        } catch (RuntimeException ex) {
            // Compensation: nếu JPA rollback/exception thì xóa message đã lưu ở Mongo
            messageRepository.deleteById(message.getId());
            throw ex;
        }
    }

    // Hàm xử lý gửi tin nhắn dạng MEDIA (File, Video, Image)
    //
    // Quy trình thực hiện:
    // 1. Upload file media lên S3 (AWS)
    // 2. Nhận lại URL và gán vào content của message
    // 3. Gọi hàm sendMessage() để xử lý lưu tin nhắn
    //
    // Nếu quá trình gửi tin nhắn xảy ra lỗi:
    // + Xóa file vừa upload khỏi S3 để tránh rác
    // + Quăng lại exception để phía trên xử lý
    @Override
    public MessageResponse sendFileMessage(
            SendMessageRequest sendMessageRequest,
            MultipartFile file
    ) {
        if (file == null)
            throw new IllegalArgumentException("File không tồn tại");

        if (sendMessageRequest.getType() == MessageType.TEXT)
            throw new IllegalArgumentException("Tin nhắn dạng TEXT không được upload file");

        UUID fileName = UUID.randomUUID();

        String url = null;
        try {
            url = s3Service.uploadFile(
                    file,
                    "chats",
                    fileName.toString(),
                    true,
                    MAX_IMAGE_SIZE
            );
            sendMessageRequest.setContent(url);
            sendMessageRequest.setFileFormat(extractFileFormat(file));

            return sendMessage(sendMessageRequest);
        } catch (Exception ex) {
            if (url != null) s3Service.deleteFileByUrl(url);
            throw ex;
        }
    }

    @Override
    public MessageResponse sendImageBatchMessage(
            SendMessageRequest sendMessageRequest,
            List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Danh sách ảnh không được để trống");
        }

        if (sendMessageRequest.getType() != MessageType.IMAGE) {
            throw new IllegalArgumentException("Chỉ hỗ trợ nhiều ảnh cho tin nhắn IMAGE");
        }

        List<String> uploadedUrls = new ArrayList<>();
        try {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    throw new IllegalArgumentException("Ảnh không hợp lệ");
                }

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("Chỉ hỗ trợ upload nhiều ảnh");
                }

                String url = s3Service.uploadFile(
                        file,
                        "chats",
                        UUID.randomUUID().toString(),
                        true,
                        MAX_IMAGE_SIZE
                );
                uploadedUrls.add(url);
            }

            sendMessageRequest.setContent(objectMapper.writeValueAsString(uploadedUrls));
            sendMessageRequest.setFileFormat(files.size() > 1 ? "image-batch" : extractFileFormat(files.getFirst()));

            return sendMessage(sendMessageRequest);
        } catch (Exception ex) {
            uploadedUrls.forEach(s3Service::deleteFileByUrl);
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }


    // Xử lý gửi tin nhắn dạng WEATHER (thời tiết).
    //
    // Quy trình thực hiện:
    // 1. Gọi WeatherService để lấy dữ liệu thời tiết theo tọa độ (lat, lon).
    // 2. Serialize dữ liệu thời tiết sang JSON và gán vào content của message.
    // 3. Tạo SendMessageRequest với type = WEATHER và tái sử dụng pipeline sendMessage().
    @Override
    public MessageResponse sendWeatherMessage(SendWeatherMessageRequest req) {
        // ============================
        // 1) Lấy dữ liệu thời tiết
        // ============================
        WeatherResponse weather = weatherService.getWeather(req.getLat(), req.getLon());

        // ============================
        // 2) Serialize JSON vào content
        // ============================
        String contentJson;
        try {
            contentJson = objectMapper.writeValueAsString(weather);
        } catch (Exception ex) {
            throw new RuntimeException("Không thể serialize dữ liệu thời tiết");
        }

        // ============================
        // 3) Tạo SendMessageRequest để tái sử dụng sendMessage()
        // ============================
        var sendReq = new SendMessageRequest();
        sendReq.setRoomId(req.getRoomId());
        sendReq.setContent(contentJson);
        sendReq.setType(MessageType.WEATHER);
        sendReq.setClientMsgId(req.getClientMsgId());
        sendReq.setRepliedMessageId(null);

        // ============================
        // 4) Gọi lại pipeline chuẩn
        // ============================
        return sendMessage(sendReq);
    }

    @Override
    public MessageResponse forwardMessage(ForwardMessageRequest request) {
        var currentUser = currentUserProvider.get();
        var senderId = currentUser.getId();
        var sourceMessage = validateForwardSourceMessage(request.getSourceMessageId(), senderId);
        UUID clientMsgId = parseClientMsgId(request.getClientMsgId());

        return forwardMessageToRoom(sourceMessage, senderId, request.getTargetRoomId(), clientMsgId);
    }

    @Override
    public List<MessageResponse> forwardMessages(ForwardMessagesRequest request) {
        var currentUser = currentUserProvider.get();
        var senderId = currentUser.getId();
        var sourceMessage = validateForwardSourceMessage(request.getSourceMessageId(), senderId);
        UUID clientMsgId = parseClientMsgId(request.getClientMsgId());

        var uniqueTargetRoomIds = request.getTargetRoomIds()
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (uniqueTargetRoomIds.isEmpty()) {
            throw new IllegalArgumentException("Danh sách phòng đích không được để trống");
        }

        List<MessageResponse> responses = new ArrayList<>();
        for (Long targetRoomId : uniqueTargetRoomIds) {
            responses.add(forwardMessageToRoom(sourceMessage, senderId, targetRoomId, clientMsgId));
        }

        return responses;
    }

    @Override
    public DeletedMessageResponse deleteMessageForMe(String messageId) {
        var currentUser = currentUserProvider.get();

        Message message = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn"));

        var memberId = new RoomMemberId(message.getRoomId(), currentUser.getId());
        var roomParticipant = roomParticipantRepository
                .findById(memberId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên của phòng chat này"));

        var deletedMessageId = new DeletedMessageId(
                message.getRoomId(),
                currentUser.getId(),
                messageId
        );

        if (!deletedMessageRepository.existsById(deletedMessageId)) {
            DeletedMessage deletedMessage = new DeletedMessage();
            deletedMessage.setId(deletedMessageId);
            deletedMessage.setRoom(roomParticipant.getRoom());
            deletedMessage.setUser(currentUser);
            deletedMessageRepository.save(deletedMessage);
        }

        if (messageId.equals(roomParticipant.getLastReadMessageId())) {
            roomParticipant.setLastReadMessageId(null);
            roomParticipant.setLastReadAt(null);
        }

        return new DeletedMessageResponse(messageId);
    }

    private Message validateForwardSourceMessage(String sourceMessageId, Long senderId) {
        var sourceMessage = messageRepository
                .findById(sourceMessageId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn nguồn"));

        if (!sourceMessage.isActive()) {
            throw new IllegalArgumentException("Không thể chuyển tiếp tin nhắn đã thu hồi");
        }

        if (sourceMessage.getType() == MessageType.SYSTEM) {
            throw new IllegalArgumentException("Không thể chuyển tiếp tin nhắn hệ thống");
        }

        var sourceMemberId = new RoomMemberId(sourceMessage.getRoomId(), senderId);
        if (!roomParticipantRepository.existsById(sourceMemberId)) {
            throw new AccessDeniedException("Bạn không có quyền truy cập tin nhắn nguồn");
        }

        return sourceMessage;
    }

    private UUID parseClientMsgId(String clientMsgIdRaw) {
        try {
            return UUID.fromString(clientMsgIdRaw);
        } catch (Exception exception) {
            throw new IllegalArgumentException("clientMsg không hợp lệ");
        }
    }

    private MessageResponse forwardMessageToRoom(
            Message sourceMessage,
            Long senderId,
            Long targetRoomId,
            UUID clientMsgId
    ) {
        Room targetRoom = roomRepository
                .findById(targetRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat đích không tồn tại"));
        var targetMemberId = new RoomMemberId(targetRoomId, senderId);
        var targetParticipant = roomParticipantRepository
                .findById(targetMemberId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên của phòng chat đích"));

        var existed = messageRepository
                .findByRoomIdAndSenderIdAndClientMsgId(targetRoomId, senderId, clientMsgId)
                .orElse(null);
        if (existed != null) return chatMapper.toMessageResponseDto(existed);

        Message forwardedMessage = new Message();
        forwardedMessage.setRoomId(targetRoomId);
        forwardedMessage.setSenderId(senderId);
        forwardedMessage.setContent(sourceMessage.getContent());
        forwardedMessage.setType(sourceMessage.getType());
        forwardedMessage.setFileFormat(sourceMessage.getFileFormat());
        forwardedMessage.setClientMsgId(clientMsgId);
        forwardedMessage.setCreatedAt(LocalDateTime.now());
        forwardedMessage.setIsForwarded(true);
        forwardedMessage.setForwardedFromMessageId(sourceMessage.getId());
        forwardedMessage.setForwardedFromRoomId(sourceMessage.getRoomId());
        forwardedMessage.setForwardedFromSenderId(sourceMessage.getSenderId());

        try {
            forwardedMessage = messageRepository.save(forwardedMessage);
        } catch (DataIntegrityViolationException ex) {
            forwardedMessage = messageRepository
                    .findByRoomIdAndSenderIdAndClientMsgId(targetRoomId, senderId, clientMsgId)
                    .orElseThrow(() -> ex);
        }

        try {
            targetRoom.setLastMessageId(forwardedMessage.getId());
            targetRoom.setLastMessageAt(forwardedMessage.getCreatedAt());

            targetParticipant.setLastReadMessageId(forwardedMessage.getId());
            targetParticipant.setLastReadAt(forwardedMessage.getCreatedAt());

            var messageCreatedEvent = new MessageCreatedEvent(forwardedMessage);
            var roomUpdatedEvent = new RoomUpdatedEvent(
                    targetRoom,
                    roomParticipantRepository.findByRoom_Id(targetRoom.getId()),
                    null
            );

            eventPublisher.publishEvent(messageCreatedEvent);
            eventPublisher.publishEvent(roomUpdatedEvent);

            var dto = chatMapper.toMessageResponseDto(forwardedMessage);

            if (cacheEnabled) {
                messageCachingService.cacheNewMessage(targetRoomId, dto);
            }

            return dto;
        } catch (RuntimeException ex) {
            messageRepository.deleteById(forwardedMessage.getId());
            throw ex;
        }
    }


    /* ========================================================================== */
    /*                         CÁC HÀM XỬ LÝ THU HỒI TIN NHẮN                     */
    /* ========================================================================== */

    @Override
    public MessageRecalledResponse recallMessage(String messageId) {
        var currentUser = currentUserProvider.get();

        Message messageToRecall = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn"));

        if (!currentUser.getId().equals(messageToRecall.getSenderId()))
            throw new AccessDeniedException("Không có quyền truy cập");

        long hours = ChronoUnit.HOURS.between(messageToRecall.getCreatedAt(), LocalDateTime.now());
        if (hours > 24)
            throw new IllegalArgumentException("Bạn chỉ có thể thu hồi tin nhắn trong vòng 24 giờ");

        // Disable message
        messageToRecall.setIsActive(false);

        deleteMessageMediaSafely(messageToRecall);

        // Xóa content
        messageToRecall.setContent("");

        // Mongo document does not use JPA dirty checking, so persist the recall explicitly.
        messageRepository.save(messageToRecall);

        // ---------------------------
        // UPDATE CACHE
        // ---------------------------
        Long roomId = messageToRecall.getRoomId();
        var dto = chatMapper.toMessageResponseDto(messageToRecall);

        if (cacheEnabled)
            messageCachingService.updateMessage(roomId, messageId, dto);


        // ---------------------------
        // WEBSOCKET EVENT
        // ---------------------------
        var messageRecalledEvent = new MessageRecalledEvent(messageId, roomId);
        eventPublisher.publishEvent(messageRecalledEvent);

        return new MessageRecalledResponse(messageId);
    }

    private void deleteMessageMediaSafely(Message message) {
        if (message.getType() == MessageType.TEXT
                || message.getType() == MessageType.SYSTEM
                || message.getType() == MessageType.WEATHER) {
            return;
        }

        String content = message.getContent();
        if (content == null || content.isBlank()) {
            return;
        }

        if (message.getType() == MessageType.IMAGE) {
            for (String mediaUrl : extractMediaUrls(content)) {
                tryDeleteFileByUrl(mediaUrl, message.getId());
            }
            return;
        }

        tryDeleteFileByUrl(content, message.getId());
    }

    /* ========================================================================== */
    /*              CÁC HÀM XỬ LÝ NGƯỜI DÙNG ĐÃ XEM TIN NHẮN                     */
    /* ========================================================================== */

    @Override
    public ReadStateResponse markAsRead(MarkReadRequest markReadRequest) {
        var currentUser = currentUserProvider.get();

        Long userId = currentUser.getId();
        Long roomId = markReadRequest.getRoomId();
        String lastReadMessageId = markReadRequest.getLastReadMessageId();

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, userId);
        RoomParticipant roomParticipant = roomParticipantRepository
                .findById(roomMemberId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên của phòng chat này"));

        var msgOpt = messageRepository.findById(lastReadMessageId);
        if (msgOpt.isEmpty() || !msgOpt.get().getRoomId().equals(roomId))
            throw new IllegalArgumentException("tin nhắn không thuộc phòng này");

        Message lastReadMessage = msgOpt.get();
        String newPointer = lastReadMessageId;

        if (roomParticipant.getLastReadMessageId() != null) {
            Optional<Message> oldMsgOpt = messageRepository.findById(roomParticipant.getLastReadMessageId());
            if (oldMsgOpt.isPresent()) {
                Message oldMessage = oldMsgOpt.get();

                if (lastReadMessage.getCreatedAt().isBefore(oldMessage.getCreatedAt())) {
                    newPointer = roomParticipant.getLastReadMessageId();
                }
            }
        }

        roomParticipant.setLastReadMessageId(newPointer);
        roomParticipant.setLastReadAt(LocalDateTime.now());

        long unread = 0L;
        if (room.getLastMessageId() != null) {
            unread = messageRepository.countByRoomIdAndCreatedAtGreaterThan(
                    roomId,
                    lastReadMessage.getCreatedAt()
            );
        }

        return new ReadStateResponse(
                roomId,
                userId,
                newPointer,
                roomParticipant.getLastReadAt(),
                unread
        );
    }

    /* ========================================================================== */
    /*                  CÁC HÀM XỬ LÝ LẤY LỊCH SỬ TIN NHẮN                        */
    /* ========================================================================== */
    @Override
    public HistoryMessageResponse getHistoryMessages(
            Long roomId,
            String beforeId,
            Integer size
    ) {
        validateHistoryRequest(roomId, size);

        var currentUser = currentUserProvider.get();
        var memberId = new RoomMemberId(roomId, currentUser.getId());
        if (!roomParticipantRepository.existsById(memberId))
            throw new RuntimeException("Not a room member");

        int fixed = Math.max(1, Math.min(size, 20));
        var deletedMessageIds = getDeletedMessageIds(roomId, currentUser.getId());
        boolean hasDeletedMessages = !deletedMessageIds.isEmpty();

        if (!hasDeletedMessages) {
            var cached = loadFromCache(roomId, beforeId, fixed);
            if (cached != null) return cached;
        }

        var db = loadFromDbCursor(roomId, beforeId, fixed, deletedMessageIds);
        if (!hasDeletedMessages) {
            cacheHistoryPage(roomId, beforeId, db);
        }

        return db;
    }


    private void validateHistoryRequest(Long roomId, Integer size) {
        if (roomId == null || size == null)
            throw new IllegalArgumentException("Invalid parameters");
    }

    private HistoryMessageResponse loadFromCache(Long roomId, String beforeId, int size) {
        if (!cacheEnabled) return null;

        var cached = messageCachingService.getMessages(roomId, beforeId, size);
        if (cached.isEmpty()) return null;

        String nextBeforeId = cached.getLast().getId();
        boolean hasMore = cached.size() == size;

        return new HistoryMessageResponse(cached, hasMore, nextBeforeId);
    }

    private void cacheHistoryPage(Long roomId, String beforeId, HistoryMessageResponse db) {
        if (!cacheEnabled || db.getMessageResponses().isEmpty()) return;

        if (beforeId == null)
            messageCachingService.cacheMessages(roomId, db.getMessageResponses());
        else
            messageCachingService.appendOlderMessages(roomId, db.getMessageResponses());
    }

    private HistoryMessageResponse loadFromDbCursor(
            Long roomId,
            String beforeId,
            int size,
            Set<String> deletedMessageIds
    ) {
        List<Message> selectedMessages = new ArrayList<>();
        String cursor = beforeId;
        boolean hasMore = false;

        while (selectedMessages.size() < size) {
            Pageable limit = PageRequest.of(0, size + 1);
            List<Message> fetched;

            if (cursor == null) {
                fetched = messageRepository.findByRoomIdOrderByIdDesc(roomId, limit);
            } else {
                fetched = messageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(roomId, cursor, limit);
            }

            if (fetched.isEmpty()) {
                hasMore = false;
                break;
            }

            hasMore = fetched.size() > size;
            List<Message> trimmed = hasMore ? fetched.subList(0, size) : fetched;

            for (Message message : trimmed) {
                if (!deletedMessageIds.contains(message.getId())) {
                    selectedMessages.add(message);
                    if (selectedMessages.size() == size) break;
                }
            }

            if (!hasMore || trimmed.isEmpty()) {
                break;
            }

            cursor = trimmed.getLast().getId();
        }

        List<MessageResponse> responses = new ArrayList<>(
                selectedMessages.stream()
                        .limit(size)
                        .map(chatMapper::toMessageResponseDto)
                        .toList()
        );

        String nextBeforeId = null;
        if (!responses.isEmpty()) {
            nextBeforeId = responses.getLast().getId();
        }

        Collections.reverse(responses);

        return new HistoryMessageResponse(responses, hasMore, nextBeforeId);
    }

    private Set<String> getDeletedMessageIds(Long roomId, Long userId) {
        return deletedMessageRepository.findByIdRoomIdAndIdUserId(roomId, userId)
                .stream()
                .map(entry -> entry.getId().getMessageId())
                .collect(Collectors.toSet());
    }

    /* ========================================================================== */
    /*                  TẠO TIN NHẮN HỆ THỐNG                                     */
    /* ========================================================================== */
    @Override
    public Message createSystemMessage(Room room, String content, User user) {
        var msg = new Message();
        msg.setRoomId(room.getId());
        msg.setSenderId(user.getId());
        msg.setType(MessageType.SYSTEM);
        msg.setContent(content);
        msg.setIsActive(true);
        msg.setCreatedAt(LocalDateTime.now());
        msg.setClientMsgId(UUID.randomUUID());


        var saved = messageRepository.save(msg);
        var dto = chatMapper.toMessageResponseDto(saved);

        if (cacheEnabled)
            messageCachingService.cacheNewMessage(room.getId(), dto);


        return saved;
    }


    /* ========================================================================== */
    /*                           CÁC HÀM HỖ TRỢ KHÁC                              */
    /* ========================================================================== */
    private static void validateUrl(String url) {
        try {
            URI u = URI.create(url);
            if (u.getScheme() == null || u.getHost() == null)
                throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
        }
    }

    private List<String> extractMediaUrls(String content) {
        String trimmed = content.trim();
        if (!trimmed.startsWith("[")) {
            return List.of(content);
        }

        try {
            List<String> urls = objectMapper.readValue(
                    trimmed,
                    new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                    }
            );
            return urls == null ? List.of() : urls;
        } catch (Exception ex) {
            log.warn("Không thể parse danh sách media để thu hồi: {}", ex.getMessage());
            return List.of(content);
        }
    }

    private void tryDeleteFileByUrl(String url, String messageId) {
        if (url == null || url.isBlank()) {
            return;
        }

        try {
            s3Service.deleteFileByUrl(url);
        } catch (RuntimeException ex) {
            log.warn("Bỏ qua lỗi xóa media khi thu hồi message {}: {}", messageId, ex.getMessage());
        }
    }

    private void validateImageContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
        }

        String trimmed = content.trim();
        if (!trimmed.startsWith("[")) {
            validateUrl(content);
            return;
        }

        try {
            List<String> urls = objectMapper.readValue(
                    trimmed,
                    new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                    }
            );

            if (urls.isEmpty()) {
                throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
            }

            for (String url : urls) {
                validateUrl(url);
            }
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Dữ liệu phải là URL hợp lệ");
        }
    }

    private void validateReplyMessage(String repliedMessageId, Long roomId) {
        if (repliedMessageId == null || repliedMessageId.isBlank()) {
            return;
        }

        Message repliedMessage = messageRepository
                .findById(repliedMessageId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn được trả lời"));

        if (!Objects.equals(repliedMessage.getRoomId(), roomId)) {
            throw new IllegalArgumentException("Tin nhắn trả lời không thuộc phòng này");
        }
    }

    private static String extractFileFormat(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
            }
        }

        String contentType = file.getContentType();
        if (contentType != null) {
            int slashIndex = contentType.lastIndexOf('/');
            if (slashIndex >= 0 && slashIndex < contentType.length() - 1) {
                return contentType.substring(slashIndex + 1).toLowerCase(Locale.ROOT);
            }
        }

        return null;
    }

    private void publishUserChatAudit(long senderId, String message) {
        try {
            UserChatEvent event = new UserChatEvent(
                    senderId,
                    message,
                    System.currentTimeMillis()
            );

            kafkaObjectTemplate.send(userChatTopic, event)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Kafka: Send event senderId {} send message {}", senderId, message);
                        } else {
                            log.error("Kafka: Send event failed: {}", ex.getMessage());
                        }
                    });

        } catch (Exception ex) {
            log.error("Error Kafka event: {}", ex.getMessage());
        }
    }
}
