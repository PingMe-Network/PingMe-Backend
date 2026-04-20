package org.ping_me.dto.response.call;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ping_me.dto.request.call.SignalingPayload;

/**
 * @author Le Tran Gia Huy
 * @created 30/11/2025 - 10:38 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.dto.response.call
 */
@Data
@AllArgsConstructor
public class SignalingResponse {
    private String type;
    private Long senderId; // Quan trọng: Để FE biết ai đang gọi
    private String senderName;    // FE khỏi phải lookup thêm
    private Long roomId;
    private String callSessionId; // Quan trọng cho N-N: FE dùng để match đúng session
    // Snapshot tại thời điểm gửi signal
    // FE dùng để hiển thị "3 người đang trong call"
    private Integer activeParticipantCount;
    private SignalingPayload payload;
}
