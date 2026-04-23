package org.ping_me.websocket.publisher;

import lombok.RequiredArgsConstructor;
import org.ping_me.service.chat.event.room.RoomCreatedEvent;
import org.ping_me.service.chat.event.room.RoomDeletedEvent;
import org.ping_me.service.chat.event.room.RoomMemberAddedEvent;
import org.ping_me.service.chat.event.room.RoomMemberRemovedEvent;
import org.ping_me.service.chat.event.room.RoomMemberRoleChangedEvent;
import org.ping_me.service.chat.event.room.RoomUpdatedEvent;
import org.ping_me.utils.mapper.ChatMapper;
import org.ping_me.websocket.WebSocketDestinations;
import org.ping_me.websocket.dto.chat.room.RoomCreatedEventPayload;
import org.ping_me.websocket.dto.chat.room.RoomDeletedEventPayload;
import org.ping_me.websocket.dto.chat.room.RoomMemberAddedEventPayload;
import org.ping_me.websocket.dto.chat.room.RoomMemberRemovedEventPayload;
import org.ping_me.websocket.dto.chat.room.RoomMemberRoleChangedEventPayload;
import org.ping_me.websocket.dto.chat.room.RoomUpdatedEventPayload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ChatRoomEventPublisher {

    private final RedisWsPublisher redisWsPublisher;
    private final ChatMapper chatMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoomUpdated(RoomUpdatedEvent event) {
        var room = event.getRoom();
        var participants = event.getRoomParticipants();

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomUpdatedEventPayload(
                    chatMapper.toRoomResponseDto(room, participants),
                    event.getSystemMessage() != null
                            ? chatMapper.toMessageResponseDto(event.getSystemMessage())
                            : null
            );

            redisWsPublisher.publish(WebSocketDestinations.userQueueRooms(userId), payload);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoomCreated(RoomCreatedEvent event) {
        var room = event.getRoom();
        var participants = event.getParticipants();

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomCreatedEventPayload(
                    chatMapper.toRoomResponseDto(room, participants)
            );

            redisWsPublisher.publish(WebSocketDestinations.userQueueRooms(userId), payload);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRoomDeleted(RoomDeletedEvent event) {
        var payload = new RoomDeletedEventPayload(
                event.getRoom().getId(),
                event.getActorUserId()
        );

        for (Long userId : event.getParticipants().stream().map(p -> p.getUser().getId()).toList()) {
            redisWsPublisher.publish(WebSocketDestinations.userQueueRooms(userId), payload);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberAdded(RoomMemberAddedEvent event) {
        var room = event.getRoom();
        var participants = event.getRoomParticipants();
        var sysMsgDto = chatMapper.toMessageResponseDto(event.getSystemMessage());

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomMemberAddedEventPayload(
                    chatMapper.toRoomResponseDto(room, participants),
                    event.getTargetUserId(),
                    event.getActorUserId(),
                    sysMsgDto
            );

            redisWsPublisher.publish(WebSocketDestinations.userQueueRooms(userId), payload);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberRemoved(RoomMemberRemovedEvent event) {
        var room = event.getRoom();
        var participants = event.getParticipants();
        var sysMsgDto = chatMapper.toMessageResponseDto(event.getSystemMessage());

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomMemberRemovedEventPayload(
                    chatMapper.toRoomResponseDto(room, participants),
                    event.getTargetUserId(),
                    event.getActorUserId(),
                    sysMsgDto
            );

            redisWsPublisher.publish(WebSocketDestinations.userQueueRooms(userId), payload);
        }

        var targetPayload = new RoomMemberRemovedEventPayload(
                chatMapper.toRoomResponseDto(room, participants),
                event.getTargetUserId(),
                event.getActorUserId(),
                sysMsgDto
        );

        redisWsPublisher.publish(
                WebSocketDestinations.userQueueRooms(event.getTargetUserId()),
                targetPayload
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onMemberRoleChanged(RoomMemberRoleChangedEvent event) {
        var room = event.getRoom();
        var participants = event.getParticipants();
        var sysMsgDto = chatMapper.toMessageResponseDto(event.getSystemMessage());

        for (Long userId : participants.stream().map(p -> p.getUser().getId()).toList()) {
            var payload = new RoomMemberRoleChangedEventPayload(
                    chatMapper.toRoomResponseDto(room, participants),
                    event.getTargetUserId(),
                    event.getOldRole(),
                    event.getNewRole(),
                    event.getActorUserId(),
                    sysMsgDto
            );

            redisWsPublisher.publish(WebSocketDestinations.userQueueRooms(userId), payload);
        }
    }
}
