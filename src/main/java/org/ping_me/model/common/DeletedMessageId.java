package org.ping_me.model.common;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeletedMessageId implements Serializable {
    private Long roomId;
    private Long userId;
    private String messageId;
}
