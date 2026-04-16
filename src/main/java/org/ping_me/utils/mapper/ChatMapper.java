package org.ping_me.utils.mapper;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ping_me.dto.response.chat.message.ForwardMetadataResponse;
import org.ping_me.dto.response.chat.message.MessageResponse;
import org.ping_me.dto.response.chat.message.RepliedMessageResponse;
import org.ping_me.dto.response.chat.room.RoomParticipantResponse;
import org.ping_me.dto.response.chat.room.RoomResponse;
import org.ping_me.model.chat.Message;
import org.ping_me.model.chat.Room;
import org.ping_me.model.chat.RoomParticipant;
import org.ping_me.model.constant.MessageType;
import org.ping_me.repository.mongodb.chat.MessageRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Admin 8/30/2025
 *
 **/
@Component
@RequiredArgsConstructor
public class ChatMapper {

    private final MessageRepository messageRepository;

    public MessageResponse toMessageResponseDto(Message message) {

        String clientMsgId = message.getClientMsgId() == null ? null : message.getClientMsgId().toString();
        String content = message.isActive() ? message.getContent() : null;
        List<String> mediaUrls = extractMediaUrls(message.getType(), content);
        ForwardMetadataResponse forwardMetadata = null;
        RepliedMessageResponse repliedMessage = mapRepliedMessage(message.getRepliedMessageId());

        if (message.isForwarded()) {
            forwardMetadata = new ForwardMetadataResponse(
                    message.getForwardedFromMessageId(),
                    message.getForwardedFromRoomId(),
                    message.getForwardedFromSenderId()
            );
        }

        return new MessageResponse(
                message.getId(),
                message.getRoomId(),
                clientMsgId,
                message.getSenderId(),
                content,
                message.getType(),
                message.getFileFormat(),
                mediaUrls,
                message.getCreatedAt(),
                message.isEdited(),
                message.getEditedAt(),
                message.isActive(),
                message.isForwarded(),
                forwardMetadata,
                repliedMessage
        );
    }

    public RoomResponse toRoomResponseDto(
            Room room,
            List<RoomParticipant> roomParticipants
    ) {
        // Lấy danh sách thành viên trong phòng chat
        List<RoomParticipantResponse> roomParticipantResponses = roomParticipants
                .stream()
                .map(rp -> new RoomParticipantResponse(
                        rp.getUser().getId(),
                        rp.getUser().getName(),
                        rp.getUser().getAvatarUrl(),
                        rp.getUser().getStatus(),
                        rp.getRole(),
                        rp.getLastReadMessageId(),
                        rp.getLastReadAt()
                ))
                .toList();

        return toRoomResponse(room, roomParticipantResponses);
    }

    private RoomResponse toRoomResponse(
            Room room,
            List<RoomParticipantResponse> roomParticipantResponses
    ) {
        RoomResponse.LastMessage lastMessage = null;
        if (room.getLastMessageId() != null) {

            Message message = messageRepository
                    .findById(room.getLastMessageId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tin nhắn"));

            lastMessage = new RoomResponse.LastMessage(
                    message.getId(),
                    message.getSenderId(),
                    message.getContent(),
                    message.getType(),
                    message.getCreatedAt()
            );
        }

        RoomResponse res = new RoomResponse();
        res.setRoomId(room.getId());
        res.setRoomType(room.getRoomType());
        res.setDirectKey(room.getDirectKey());
        res.setName(room.getName());
        res.setLastMessage(lastMessage);
        res.setParticipants(roomParticipantResponses);
        res.setRoomImgUrl(room.getRoomImgUrl());
        res.setTheme(room.getTheme());
        return res;
    }

    private List<String> extractMediaUrls(MessageType type, String content) {
        if (type != MessageType.IMAGE || content == null || content.isBlank()) {
            return null;
        }

        String trimmed = content.trim();
        if (!trimmed.startsWith("[")) {
            return List.of(content);
        }

        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(
                    trimmed,
                    new com.fasterxml.jackson.core.type.TypeReference<ArrayList<String>>() {
                    }
            );
        } catch (Exception ex) {
            return List.of(content);
        }
    }

    private RepliedMessageResponse mapRepliedMessage(String repliedMessageId) {
        if (repliedMessageId == null || repliedMessageId.isBlank()) {
            return null;
        }

        return messageRepository.findById(repliedMessageId)
                .map(message -> new RepliedMessageResponse(
                        message.getId(),
                        message.getSenderId(),
                        message.isActive() ? message.getContent() : null,
                        message.getType(),
                        message.isActive(),
                        message.getFileFormat(),
                        extractMediaUrls(message.getType(), message.isActive() ? message.getContent() : null)
                ))
                .orElse(null);
    }

}
