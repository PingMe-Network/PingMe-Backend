package org.ping_me.dto.response.chat.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 4/23/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PollOptionResponse {

    private String id;
    private String text;
    private Integer voteCount;
    private java.util.List<Long> voterIds;
}
