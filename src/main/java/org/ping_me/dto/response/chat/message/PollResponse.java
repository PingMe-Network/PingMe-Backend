package org.ping_me.dto.response.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Admin 4/23/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PollResponse {

    private String question;
    private Boolean allowMultiple;
    private LocalDateTime expiresAt;
    private Boolean expired;
    private Integer totalVotes;
    private List<PollOptionResponse> options;
}
