package me.huynhducphu.PingMe_Backend.model.chat;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;
import me.huynhducphu.PingMe_Backend.model.common.RoomMemberId;
import me.huynhducphu.PingMe_Backend.model.constant.RoomRole;

import java.time.LocalDateTime;

/**
 * Admin 8/10/2025
 **/
@Entity
@Table(
        name = "room_participants",
        indexes = {
                @Index(name = "idx_rp_user", columnList = "user_id"),
                @Index(name = "idx_rp_room_user", columnList = "room_id, user_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class RoomParticipant extends BaseEntity {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private RoomMemberId id;

    @Enumerated(EnumType.STRING)
    private RoomRole role = RoomRole.MEMBER;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @MapsId("roomId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
