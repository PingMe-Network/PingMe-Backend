package me.huynhducphu.PingMe_Backend.controller.call;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.call.SignalingRequest;
import me.huynhducphu.PingMe_Backend.service.call.impl.SignalingServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Le Tran Gia Huy
 * @created 01/12/2025 - 5:56 PM
 * @project DHKTPM18ATT_Nhom10_PingMe_Backend
 * @package me.huynhducphu.PingMe_Backend.controller.call
 */
@RestController
@RequestMapping("/chat/signaling")
@RequiredArgsConstructor
public class SignalingController {

    private final SignalingServiceImpl signalingService;

    @PostMapping
    public ResponseEntity<Void> sendSignal(@RequestBody SignalingRequest request) {
        signalingService.processSignaling(request);
        System.out.println("Signaling message processed: " + request);
        return ResponseEntity.ok().build();
    }
}
