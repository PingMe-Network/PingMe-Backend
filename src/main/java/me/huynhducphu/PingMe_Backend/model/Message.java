package me.huynhducphu.PingMe_Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.common.BaseEntity;
import me.huynhducphu.PingMe_Backend.model.constant.MessageType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Admin 8/10/2025
 **/
@Entity
@Table(
        name = "messages",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_msg_idem",
                        columnNames = {"room_id", "sender_id", "client_msg_id"}
                )
        },
        indexes = {
                @Index(name = "idx_msg_room_created_id", columnList = "room_id, created_at DESC, id DESC"),
                @Index(name = "idx_msg_room_id_id_desc ", columnList = "room_id, id DESC"),
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Message extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 1000, nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "client_msg_id", columnDefinition = "BINARY(16)", updatable = false, nullable = false)
    private UUID clientMsgId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;


}
