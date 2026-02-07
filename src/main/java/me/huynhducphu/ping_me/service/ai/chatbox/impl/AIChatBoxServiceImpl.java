package me.huynhducphu.ping_me.service.ai.chatbox.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.ping_me.model.ai.AIChatRoom;
import me.huynhducphu.ping_me.model.ai.AIMessage;
import me.huynhducphu.ping_me.model.constant.AIMessageType;
import me.huynhducphu.ping_me.repository.mongodb.ai.AIChatRoomRepository;
import me.huynhducphu.ping_me.repository.mongodb.ai.AIMessageRepository;
import me.huynhducphu.ping_me.service.ai.chatbox.AIChatBoxService;
import me.huynhducphu.ping_me.service.s3.S3Service;
import me.huynhducphu.ping_me.service.user.CurrentUserProvider;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

import static me.huynhducphu.ping_me.service.music.SongService.MAX_COVER_SIZE;

/**
 * @author Le Tran Gia Huy
 * @created 27/01/2026 - 5:54 PM
 * @project PingMe-Backend
 * @package me.huynhducphu.ping_me.service.ai.chatbox
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AIChatBoxServiceImpl implements AIChatBoxService {

    Long MAX_IMG_SIZE = 5L * 1024L * 1024L;
    private final ChatClient chatClient;
    private final S3Service s3Service;
    private final AIMessageRepository aiMessageRepository;
    private final AIChatRoomRepository aiChatRoomRepository;
    private final CurrentUserProvider currentUserProvider;

    public String sendMessageToAI(UUID chatRoomId, String prompt, List<MultipartFile> files) {
        //Kiểm tra tính hợp lệ của file (phải là ảnh + dung lượng < 5MB)
        validateFiles(files);
        validatePrompt(prompt);
        Long userId = currentUserProvider.get().getId();
        if(chatRoomId == null) {
            AIChatRoom newChatRoom = new AIChatRoom();
            newChatRoom.setId(UUID.randomUUID());
            newChatRoom.setUserId(userId);
            // Lưu chat room mới vào database
            chatRoomId = newChatRoom.getId();
            aiChatRoomRepository.save(newChatRoom);
        }else if (!aiMessageRepository.existsById(chatRoomId)) {
            throw new RuntimeException("Chat room không tồn tại!");
        }
        // Tải file lên S3 và lưu URL vào database
        List<AIMessage.Attachment> dbAttachments = processingMediaFile(files);
        // Chuyển đổi MultipartFile thành Media (Media thuộc Spring AI)
        List<Media> mediaList = (files != null) ? files.stream().map(file -> {
            return new Media(
                    MimeTypeUtils.parseMimeType(Objects.requireNonNull(file.getContentType())),
                    file.getResource()
            );
        }).toList() : List.of();
        //tạo 1 AIMessage lưu prompt và media vào database
        AIMessage humanMessage = new AIMessage();
        humanMessage.setId(UUID.randomUUID());
        humanMessage.setChatRoomId(chatRoomId);
        humanMessage.setUserId(userId);
        humanMessage.setType(AIMessageType.SENT);
        humanMessage.setContent(prompt);
        humanMessage.setAttachments(dbAttachments);
        aiMessageRepository.save(humanMessage);
        /**
         *Tạo 1 UserMessage bao gồm prompt và media. UserMessage thuộc Spring (org.springframework.ai.chat.messages)
         *Từ đó spring gửi cả UserMessage đó đến OpenAI thông qua ChatClient
         */
        //Gọi ChatClient để gửi prompt và nhận phản hồi từ AI
        String response = getResponseFromAI(userId, chatRoomId, prompt, mediaList);
        AIMessage aiMessage = new AIMessage();
        aiMessage.setId(UUID.randomUUID());
        aiMessage.setChatRoomId(chatRoomId);
        aiMessage.setType(AIMessageType.RECEIVED);
        aiMessage.setContent(response);
        aiMessageRepository.save(aiMessage);
        // Cập nhật thông tin phòng chat
        AIChatRoom currentChatRoom = aiChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Chat room không tồn tại!"));;
        currentChatRoom.setMsgCountSinceLastSummary(currentChatRoom.getMsgCountSinceLastSummary()+1);
        currentChatRoom.setTotalMsgCount(currentChatRoom.getTotalMsgCount()+1);

        if(currentChatRoom.getTitle().trim().isEmpty()){
            String titlePrompt = getTitleForAICHatRoom(prompt, response);
            currentChatRoom.setTitle(titlePrompt);
        }else{
            // Cứ sau mỗi 10 tin nhắn, ta sẽ yêu cầu GPT-5 Nano tóm tắt cuộc trò chuyện để làm ký ức dài hạn
            if(currentChatRoom.getMsgCountSinceLastSummary() >= 20){
                // Gọi hàm tóm tắt hội thoại (chạy ngầm, không block luồng chính)
                checkAndSummarize(chatRoomId);
            }

        }
        return response;
    }

    private String getResponseFromAI(Long userId, UUID currentRoomId, String prompt, List<Media> mediaList) {
        // 1. Lấy 20 tin nhắn
        List<AIMessage> currentRoomMsgs = getCurrentRoomHistory(currentRoomId, 0, 20);
        // 2. Lấy 10 tin nhắn phòng khác
        List<AIMessage> otherRoomsMsgs = getOtherMessageHistoryFromAnotherRooms(userId, currentRoomId, 0, 10);

        Collections.reverse(currentRoomMsgs);
        Collections.reverse(otherRoomsMsgs);

        String actualPrompt = buildContextualPrompt(
                prompt,
                currentRoomMsgs,
                otherRoomsMsgs,
                //???
                aiChatRoomRepository.findById(currentRoomId).get().getLatestSummary()
        );
        return useAi(actualPrompt, List.of(),"gpt-5-mini", 500);
    }

    private List<AIMessage> getCurrentRoomHistory(
            UUID currentRoomId,
            int pageNumber,
            int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<AIMessage> currentRoomMsgs = new ArrayList<>(
                aiMessageRepository
                        .findByChatRoomIdOrderByCreatedAtDesc(currentRoomId, pageable)
                        .getContent()
        );
        Collections.reverse(currentRoomMsgs);
        return currentRoomMsgs;
    }

    private List<AIMessage> getOtherMessageHistoryFromAnotherRooms(
            Long userId,
            UUID currentRoomId,
            int pageNumber,
            int pageSize)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<AIMessage> otherRoomsMsgs = new ArrayList<>(
                aiMessageRepository
                        .findByUserIdAndChatRoomIdNotOrderByCreatedAtDesc(userId, currentRoomId, pageable)
                        .getContent()
        );
        Collections.reverse(otherRoomsMsgs);
        return otherRoomsMsgs;
    }

    public String buildContextualPrompt(
            String userQuestion,
            List<AIMessage> currentRoomHistory,
            List<AIMessage> otherRoomsHistory,
            String summary
    ) {
        StringBuilder prompt = new StringBuilder();
        // 1. System Instruction (Định hình vai trò)
        prompt.append("System: Bạn là trợ lý AI hữu ích trong ứng dụng PingMe.\n");
        prompt.append("Nhiệm vụ: Trả lời câu hỏi người dùng dựa trên các ngữ cảnh được cung cấp dưới đây.\n\n");
        // 2. Ký ức dài hạn (Summary)
        prompt.append("<long_term_memory>\n");
        prompt.append(summary != null && !summary.isEmpty() ? summary : "Chưa có tóm tắt nào.");
        prompt.append("\n</long_term_memory>\n\n");
        // 3. Ngữ cảnh chéo từ phòng khác (Cross-context)
        prompt.append("<related_context_from_other_rooms>\n");
        prompt.append(formatHistory(otherRoomsHistory));
        prompt.append("</related_context_from_other_rooms>\n\n");
        // 4. Lịch sử hội thoại hiện tại (Short-term memory)
        prompt.append("<current_conversation_history>\n");
        prompt.append(formatHistory(currentRoomHistory));
        prompt.append("</current_conversation_history>\n\n");
        // 5. Câu hỏi hiện tại (Trigger)
        prompt.append("User Question: ").append(userQuestion).append("\n");
        prompt.append("AI Answer:");
        return prompt.toString();
    }

    private String buildSummarizationPrompt(String currentSummary, List<AIMessage> newMessages) {
        StringBuilder sb = new StringBuilder();
        sb.append("System: Bạn là chuyên gia tóm tắt hội thoại cho ứng dụng PingMe.\n");
        sb.append("Nhiệm vụ: Cập nhật bản tóm tắt hiện tại dựa trên các tin nhắn mới nhất. Giữ lại các chi tiết quan trọng về sở thích, thông tin cá nhân hoặc vấn đề đang thảo luận của người dùng. Bỏ qua các câu chào hỏi xã giao.\n\n");
        // 1. Ký ức cũ
        sb.append("<current_summary>\n");
        sb.append(currentSummary != null && !currentSummary.isEmpty() ? currentSummary : "Chưa có dữ liệu.");
        sb.append("\n</current_summary>\n\n");
        // 2. Hội thoại mới cần gộp vào
        sb.append("<new_messages_to_merge>\n");
        for (AIMessage msg : newMessages) {
            String role = (msg.getType() == AIMessageType.SENT) ? "User" : "AI";
            sb.append(String.format("[%s]: %s\n", role, msg.getContent()));
        }
        sb.append("</new_messages_to_merge>\n\n");
        sb.append("Yêu cầu output: Chỉ trả về đoạn văn bản tóm tắt mới (ngắn gọn, súc tích). Không trả về lời dẫn.");
        return sb.toString();
    }

    @Async // Chạy ngầm, không block luồng chính
    @Transactional
    protected void checkAndSummarize(UUID chatRoomId) {
        // 1. Lấy thông tin phòng chat
        AIChatRoom room = aiChatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        // 2. Kiểm tra điều kiện: Nếu chưa đủ 10 tin nhắn mới thì bỏ qua
        if (room.getMsgCountSinceLastSummary() < 20) {
            return;
        }
        log.info("Triggering summary for Room: {}", chatRoomId);

        try {
            // 3. Lấy 20 tin nhắn gần nhất (10 cặp hội thoại)
            List<AIMessage> recentMessages = getCurrentRoomHistory(chatRoomId, 0, 20);
            if (recentMessages.isEmpty()) return;
            // 4. Build Prompt: Gộp Summary Cũ + Tin nhắn Mới
            String prompt = buildSummarizationPrompt(
                    room.getLatestSummary(),
                    recentMessages
            );
            // 5. Gọi AI để tạo Summary Mới
            String newSummary = useAi(prompt, List.of(),"gpt-5-nano", 100);
            // 6. Cập nhật và Lưu vào DB
            room.setLatestSummary(newSummary);
            room.setMsgCountSinceLastSummary(0); // Reset bộ đếm về 0
            aiChatRoomRepository.save(room);
            log.info("Summary updated successfully for Room: {}", chatRoomId);
        } catch (Exception e) {
            log.error("Failed to summarize chat room: {}", chatRoomId, e);
            // Lưu ý: Nếu lỗi thì KHÔNG reset msgCount để lần sau thử lại
        }
    }

    // Helper: Chuyển List<AIMessage> thành String dễ đọc
    private String formatHistory(List<AIMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return "(Không có dữ liệu)\n";
        }
        StringBuilder historyBuilder = new StringBuilder();
        for (AIMessage msg : messages) {
            // Xác định vai trò: User hay AI
            String role = (msg.getType() == AIMessageType.SENT) ? "User" : "AI";
            // Format: [User]: Nội dung tin nhắn
            historyBuilder.append(String.format("[%s]: %s\n", role, msg.getContent()));
        }
        return historyBuilder.toString();
    }

    private String getTitleForAICHatRoom(String sentPrompt, String receivedResponse){
        String titlePrompt = "Đặt tiêu đề cho chat: User: " + sentPrompt + " | AI: " + receivedResponse
                + ". Quy tắc: < 10 từ, không dùng ngoặc kép, chỉ trả về text tiêu đề. Ngôn ngữ trả về tùy thuộc vào ngôn ngữ của prompt. LƯU Ý, CHỈ TRẢ VỀ TIÊU ĐỀ, KHÔNG THÊM BẤT KỲ KÝ TỰ, NỘI DUNG NÀO KHÁC.";
        return useAi(titlePrompt, List.of(),"gpt-5-nano", 50);
    }

    private String useAi(String actualPrompt, List<Media> mediaList, String model, int maxTokens) {
        UserMessage userMessage = UserMessage.builder().text(actualPrompt).media(mediaList).build();
        //Cấu hình các options cho cuộc trò chuyện với AI (model và max tokens)
        var options = OpenAiChatOptions.builder().model(model).maxTokens(maxTokens).build();
        //Gọi ChatClient để gửi prompt và nhận phản hồi từ AI
        return chatClient.prompt().messages(userMessage).options(options).call().content();
    }

    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            long sizeInBytes = file.getSize();

            // 1. Chỉ cho phép các định dạng ảnh phổ biến
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Chỉ được phép tải lên hình ảnh! File "
                        + file.getOriginalFilename() + " không hợp lệ.");
            }

            // 2. Giới hạn dung lượng ảnh tối đa 5MB
            if (sizeInBytes > 5 * 1024 * 1024) {
                throw new RuntimeException("Ảnh " + file.getOriginalFilename()
                        + " vượt quá giới hạn 5MB.");
            }
        }
    }

    private void validatePrompt(String prompt){
        if (prompt.length() > 4000) {
            throw new RuntimeException("Câu hỏi quá dài! Vui lòng tóm tắt lại dưới 4000 ký tự.");
        }
    }

    private String generateFileName(MultipartFile file) {
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        return UUID.randomUUID() + ext;
    }

    private List<AIMessage.Attachment> processingMediaFile(List<MultipartFile> files){
        List<AIMessage.Attachment> dbAttachments = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileName = generateFileName(file);
                String fileUrl = s3Service.uploadFile(file, "ai/file", fileName, true, MAX_IMG_SIZE);

                // Lưu vào list attachment để lưu database
                dbAttachments.add(new AIMessage.Attachment(fileUrl, file.getContentType()));
            }
        }
        return dbAttachments;
    }

}
