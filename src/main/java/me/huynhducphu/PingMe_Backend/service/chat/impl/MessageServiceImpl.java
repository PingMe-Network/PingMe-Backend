package me.huynhducphu.PingMe_Backend.service.chat.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.MarkReadRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.HistoryMessageResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.ReadStateResponse;
import me.huynhducphu.PingMe_Backend.dto.request.chat.message.SendMessageRequest;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.MessageCreatedEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.MessageRecalledEvent;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.event.RoomUpdatedEvent;
import me.huynhducphu.PingMe_Backend.model.Message;
import me.huynhducphu.PingMe_Backend.model.Room;
import me.huynhducphu.PingMe_Backend.model.RoomParticipant;
import me.huynhducphu.PingMe_Backend.model.common.RoomMemberId;
import me.huynhducphu.PingMe_Backend.model.constant.MessageType;
import me.huynhducphu.PingMe_Backend.repository.MessageRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomParticipantRepository;
import me.huynhducphu.PingMe_Backend.repository.RoomRepository;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import me.huynhducphu.PingMe_Backend.service.chat.util.ChatDtoUtils;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import me.huynhducphu.PingMe_Backend.service.integration.S3Service;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Admin 8/26/2025
 *
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class MessageServiceImpl implements me.huynhducphu.PingMe_Backend.service.chat.MessageService {

    private static final long MAX_BLOG_IMAGE_SIZE = 10 * 1024 * 1024L;

    private final S3Service s3Service;

    private final CurrentUserProvider currentUserProvider;

    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher eventPublisher;


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

        // Nếu không dạng file này một dạng file
        // có thể chứa link qua từ s3
        if (sendMessageRequest.getType() != MessageType.TEXT)
            validateUrl(sendMessageRequest.getContent());

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

        // Kiểm tra tin nhắn người dùng gửi đã tồn tại chưa, Kiểm tra bằng mã clientMsgId
        // Nếu tìm thấy thì trả về tin nhắn đã tồn tại trong cơ sở dữ liệu
        var existed = messageRepository
                .findByRoom_IdAndSender_IdAndClientMsgId(roomId, senderId, clientMsgId)
                .orElse(null);
        if (existed != null) return ChatDtoUtils.toMessageResponseDto(existed);

        // Nếu chưa tồn tại, tạo tin nhắn mới
        Message message = new Message();
        message.setRoom(room);
        message.setSender(userRepository.getReferenceById(senderId));
        message.setContent(sendMessageRequest.getContent());
        message.setType(sendMessageRequest.getType());
        message.setClientMsgId(clientMsgId);
        message.setCreatedAt(LocalDateTime.now());

        // Lưu tin nhắn vào cơ sở dữ liệu
        // Thực hiện Try Catch để tránh Race Condition
        // cho trường hợp User dùng 2 máy gửi tin nhắn cùng lúc
        try {
            message = messageRepository.save(message);
        } catch (DataIntegrityViolationException ex) {
            message = messageRepository
                    .findByRoom_IdAndSender_IdAndClientMsgId(roomId, senderId, clientMsgId)
                    .orElseThrow(() -> ex);
        }

        // Cập nhật trạng thái phòng khi người dùng nhắn tin
        // Thông tin cập nhật bao gồm:
        // + Tin nhắn cuối cùng
        // + Thời gian nhắn tin nhắn cuối cùng
        room.setLastMessage(message);
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

        // ===================================================================================================
        // WEBSOCKET

        // Sự kiện MESSAGE_CREATED (tạo tin nhắn mới)
        var messageCreatedEvent = new MessageCreatedEvent(message);

        // Sử kiện ROOM_UPDATED (thông báo phòng có tin nhắn mới)
        var roomUpdatedEvent = new RoomUpdatedEvent(
                room,
                roomParticipantRepository.findByRoom_Id(room.getId())
        );

        // Bắn sự kiện Websocket
        eventPublisher.publishEvent(messageCreatedEvent);
        eventPublisher.publishEvent(roomUpdatedEvent);
        // ===================================================================================================

        // Trả dữ liệu về cho người gửi tin nhắn
        return ChatDtoUtils.toMessageResponseDto(message);
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
                    MAX_BLOG_IMAGE_SIZE
            );
            sendMessageRequest.setContent(url);

            return sendMessage(sendMessageRequest);
        } catch (Exception ex) {
            if (url != null) s3Service.deleteFileByUrl(url);
            throw ex;
        }
    }

    /* ========================================================================== */
    /*                         CÁC HÀM XỬ LÝ THU HỒI TIN NHẮN                     */
    /* ========================================================================== */

    @Override
    public MessageRecalledResponse recallMessage(Long messageId) {
        var currentUser = currentUserProvider.get();

        Message messageToRecall = messageRepository
                .findById(messageId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn"));

        if (!currentUser.getId().equals(messageToRecall.getSender().getId()))
            throw new AccessDeniedException("Không có quyền truy cập");

        messageToRecall.setActive(false);

        // Nếu khác Text, tất là type Media thì gọi S3 xóa
        if (!messageToRecall.getType().equals(MessageType.TEXT))
            s3Service.deleteFileByUrl(messageToRecall.getContent());

        // Xóa nội dung trong chat
        messageToRecall.setContent("");

        // ===================================================================================================
        // WEBSOCKET

        // Sự kiện MESSAGE_CREATED (tạo tin nhắn mới)
        var roomId = messageToRecall.getRoom().getId();
        var messageRecalledEvent = new MessageRecalledEvent(messageId, roomId);

        // Bắn sự kiện Websocket
        eventPublisher.publishEvent(messageRecalledEvent);
        // ===================================================================================================

        return new MessageRecalledResponse(messageId);
    }

    /* ========================================================================== */
    /*              CÁC HÀM XỬ LÝ NGƯỜI DÙNG ĐÃ XEM TIN NHẮN                     */
    /* ========================================================================== */

    @Override
    public ReadStateResponse markAsRead(MarkReadRequest markReadRequest) {
        var currentUser = currentUserProvider.get();

        Long userId = currentUser.getId();
        Long roomId = markReadRequest.getRoomId();
        Long lastReadMessageId = markReadRequest.getLastReadMessageId();

        Room room = roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat này không tồn tại"));
        var roomMemberId = new RoomMemberId(roomId, userId);
        RoomParticipant roomParticipant = roomParticipantRepository
                .findById(roomMemberId)
                .orElseThrow(() -> new AccessDeniedException("Bạn không phải thành viên của phòng chat này"));

        var msgOpt = messageRepository.findById(lastReadMessageId);
        if (msgOpt.isEmpty() || !msgOpt.get().getRoom().getId().equals(roomId))
            throw new IllegalArgumentException("tin nhắn không thuộc phòng này");

        Long newPointer = (roomParticipant.getLastReadMessageId() == null)
                ? lastReadMessageId
                : Math.max(roomParticipant.getLastReadMessageId(), lastReadMessageId);

        roomParticipant.setLastReadMessageId(newPointer);
        roomParticipant.setLastReadAt(LocalDateTime.now());

        long unread = 0L;
        if (room.getLastMessage() != null) {
            long lastMsgId = room.getLastMessage().getId();
            unread = Math.max(0, lastMsgId - newPointer);
        }

        return new ReadStateResponse(roomId, userId, newPointer, roomParticipant.getLastReadAt(), unread);
    }

    /* ========================================================================== */
    /*                  CÁC HÀM XỬ LÝ LẤY LỊCH SỬ TIN NHẮN                        */
    /* ========================================================================== */
    @Override
    public HistoryMessageResponse getHistoryMessages(
            Long roomId,
            Long beforeId,
            Integer size
    ) {
        // Kiểm tra dữ liệu đầu vào
        if (roomId == null || size == null)
            throw new IllegalArgumentException("Mã phòng hoặc số lượng tin nhắn không hợp lệ");

        // Lấy thông tin người dùng hiện tại
        var currentUser = currentUserProvider.get();
        var userId = currentUser.getId();

        // Kiểm tra phòng chat có tồn tại không
        roomRepository
                .findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Phòng chat không tồn tại"));

        // Kiểm tra người dùng có phải thành viên của phòng hay không
        var roomMemberId = new RoomMemberId(roomId, userId);
        if (!roomParticipantRepository.existsById(roomMemberId))
            throw new AccessDeniedException("Bạn không phải thành viên của phòng chat này");

        // Giới hạn số lượng tin nhắn lấy ra (1 → 20)
        int fixedSize = Math.max(1, Math.min(size, 20));
        Pageable limit = PageRequest.of(0, fixedSize);

        // Truy vấn tin nhắn từ database, sau đó map sang DTO
        Page<Message> page = messageRepository.findHistoryMessagesByKeySet(roomId, beforeId, limit);
        List<MessageResponse> messageResponses = page
                .getContent()
                .stream()
                .map(ChatDtoUtils::toMessageResponseDto)
                .toList();

        Long total = page.getTotalElements();

        return new HistoryMessageResponse(messageResponses, total);
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

}
