package org.ping_me.dto.request.call;

import lombok.Data;

/**
 * @author Le Tran Gia Huy
 * @created 19/04/2026 - 9:34 AM
 * @project PingMe-Auth-Service
 * @package org.ping_me.dto.request.call
 */
@Data
public class SignalingPayload {
    private String callType;      // "AUDIO" | "VIDEO"
    private String reason;        // Dùng cho REJECT/HANGUP: "BUSY", "DECLINED", "NO_ANSWER"
    // Nếu sau này cần thêm gì (custom data Zego, etc.) thì mở rộng ở đây
}
