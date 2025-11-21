package me.huynhducphu.PingMe_Backend.service.chat.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.PingMe_Backend.dto.response.chat.message.MessageRecalledResponse;
import me.huynhducphu.PingMe_Backend.dto.ws.chat.*;
import me.huynhducphu.PingMe_Backend.service.chat.event.*;
import me.huynhducphu.PingMe_Backend.service.chat.util.ChatDtoUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Admin 8/29/2025
 *
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /* ========================================================================== */
    /*                           MESSAGE CREATED                                  */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageCreated(MessageCreatedEvent event) {
        var messageResponse = ChatDtoUtils.toMessageResponseDto(event.getMessage());
        var roomId = event.getMessage().getRoom().getId();

        String destination = "/topic/rooms/" + roomId + "/messages";
        var payload = new MessageCreatedEventPayload(messageResponse);

        messagingTemplate.convertAndSend(destination, payload);

//        log.info("onMessageCreated → room={}, messageId={}", roomId, event.getMessage().getId());
    }

    /* ========================================================================== */
    /*                           MESSAGE RECALLED                                 */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMessageRecalled(MessageRecalledEvent event) {
        var roomId = event.getRoomId();
        var messageRecalledResponse = new MessageRecalledResponse(event.getMessageId());

        String destination = "/topic/rooms/" + roomId + "/messages";
        var payload = new MessageRecalledEventPayload(messageRecalledResponse);

        messagingTemplate.convertAndSend(destination, payload);
    }

    /* ========================================================================== */
    /*                           ROOM UPDATED                                     */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoomUpdated(RoomUpdatedEvent event) {
        var room = event.getRoom();
        var participants = event.getRoomParticipants();

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomUpdatedEventPayload(
                    ChatDtoUtils.toRoomResponseDto(room, participants, userId)
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/rooms",
                    payload
            );
        }
    }

    /* ========================================================================== */
    /*                           ROOM CREATED                                     */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoomCreated(RoomCreatedEvent event) {
        var room = event.getRoom();
        var participants = event.getParticipants();

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomCreatedEventPayload(
                    ChatDtoUtils.toRoomResponseDto(room, participants, userId)
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/rooms",
                    payload
            );
        }
    }

    /* ========================================================================== */
    /*                       ROOM MEMBER  —  ADDED                                */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberAdded(RoomMemberAddedEvent event) {

        var room = event.getRoom();
        var participants = event.getRoomParticipants();
        var sysMsgDto = ChatDtoUtils.toMessageResponseDto(event.getSystemMessage());

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {

            var payload = new RoomMemberAddedEventPayload(
                    ChatDtoUtils.toRoomResponseDto(room, participants, userId),
                    event.getTargetUserId(),
                    event.getActorUserId(),
                    sysMsgDto
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/rooms",
                    payload
            );
        }
    }


    /* ========================================================================== */
    /*                       ROOM MEMBER  —  REMOVED                              */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberRemoved(RoomMemberRemovedEvent event) {
        var room = event.getRoom();
        var participants = event.getParticipants();
        var sysMsgDto = ChatDtoUtils.toMessageResponseDto(event.getSystemMessage());

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {

            var payload = new RoomMemberRemovedEventPayload(
                    ChatDtoUtils.toRoomResponseDto(room, participants, userId),
                    event.getTargetUserId(),
                    event.getActorUserId(),
                    sysMsgDto
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/rooms",
                    payload
            );
        }
    }

    /* ========================================================================== */
    /*                           ROOM MEMBER  —  ROLE CHANGED                     */
    /* ========================================================================== */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberRoleChanged(RoomMemberRoleChangedEvent event) {
        var room = event.getRoom();
        var participants = event.getParticipants();
        var sysMsgDto = ChatDtoUtils.toMessageResponseDto(event.getSystemMessage());

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomMemberRoleChangedEventPayload(
                    ChatDtoUtils.toRoomResponseDto(room, participants, userId),
                    event.getTargetUserId(),
                    event.getOldRole(),
                    event.getNewRole(),
                    event.getActorUserId(),
                    sysMsgDto
            );

            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    "/queue/rooms",
                    payload
            );
        }
    }


}
