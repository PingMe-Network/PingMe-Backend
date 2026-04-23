package org.ping_me.model.chat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin 4/23/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Poll {

    String question;
    List<PollOption> options;
    Boolean allowMultiple = false;
    LocalDateTime expiresAt;

    public boolean allowMultiple() {
        return Boolean.TRUE.equals(allowMultiple);
    }
}
