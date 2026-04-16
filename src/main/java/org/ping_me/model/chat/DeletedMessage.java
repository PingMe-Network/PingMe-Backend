package org.ping_me.model.chat;

import jakarta.persistence.*;
import lombok.*;
import org.ping_me.model.User;
import org.ping_me.model.common.BaseEntity;
import org.ping_me.model.common.DeletedMessageId;

@Entity
@Table(
        name = "deleted_messages",
        indexes = {
                @Index(name = "idx_dm_room_user", columnList = "room_id, user_id"),
                @Index(name = "idx_dm_user", columnList = "user_id")
        }
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DeletedMessage extends BaseEntity {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private DeletedMessageId id;

    @MapsId("roomId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
