package me.huynhducphu.PingMe_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;
import me.huynhducphu.PingMe_Backend.model.constant.RoomType;

import java.time.LocalDateTime;

/**
 * Admin 8/10/2025
 **/
@Entity
@Table(name = "rooms")
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "room_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    // DIRECT (1-1), GROUP (n-n)

    @Column(name = "direct_key", unique = true)
    private String directKey;
    // ROOM KEY (Nếu chat n-n thì null)

    private String name;
    // ROOM NAME (nếu chat 1-1 thì NULL)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_message_id")
    private Message lastMessage;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;
}
