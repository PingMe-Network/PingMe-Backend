package org.ping_me.controller.call;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.ping_me.dto.request.call.SignalingRequest;
import org.ping_me.service.call.SignalingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Le Tran Gia Huy
 * @created 01/12/2025 - 5:56 PM
 */
@Tag(
        name = "Call Signaling",
        description = "Các endpoints xử lý tín hiệu cuộc gọi (WebRTC / Call realtime)"
)
@RestController
@RequestMapping("/core-service/chat/signaling")
@RequiredArgsConstructor
public class SignalingController {

    private final SignalingService signalingService;

    // ================= SIGNAL =================
    @Operation(
            summary = "Gửi tín hiệu cuộc gọi",
            description = """
        Gửi signaling message để thiết lập / điều phối cuộc gọi realtime qua Zego.
        Các loại tín hiệu hỗ trợ:
        - INVITE  : Mời người dùng vào cuộc gọi
        - ACCEPT  : Chấp nhận cuộc gọi
        - REJECT  : Từ chối cuộc gọi
        - LEAVE   : Rời cuộc gọi (call vẫn tiếp tục nếu còn người)
        - HANGUP  : Kết thúc cuộc gọi
        """
    )
    @PostMapping
    public ResponseEntity<Void> sendSignal(
            @Parameter(
                    description = "Payload signaling (SDP / ICE / metadata)",
                    required = true
            )
            @RequestBody SignalingRequest request
    ) {
        signalingService.processSignaling(request);
        return ResponseEntity.ok().build();
    }
}
