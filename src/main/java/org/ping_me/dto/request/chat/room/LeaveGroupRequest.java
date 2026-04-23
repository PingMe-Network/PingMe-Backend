package org.ping_me.dto.request.chat.room;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 4/23/2026
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LeaveGroupRequest {

    private Long newOwnerId;
}
