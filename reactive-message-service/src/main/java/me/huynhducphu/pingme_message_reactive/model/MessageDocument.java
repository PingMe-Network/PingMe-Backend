package me.huynhducphu.pingme_message_reactive.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Document("messages")
@CompoundIndexes({
        @CompoundIndex(name = "room_id_id_desc", def = "{'roomId': 1, '_id': -1}"),
        @CompoundIndex(name = "sender_client_msg_idx", def = "{'roomId': 1, 'senderId': 1, 'clientMsgId': 1}", unique = true)
})
public class MessageDocument {

    @Id
    private String id;
    private Long roomId;
    private Long senderId;
    private String content;
    private MessageType type;
    private UUID clientMsgId;
    private LocalDateTime createdAt;
    private boolean recalled;
}
