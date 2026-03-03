package org.ping_me.controller.authentication;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.ping_me.client.TurnstileClient;
import org.ping_me.client.dto.TurnstileResponse;
import org.ping_me.dto.base.ApiResponse;
import org.ping_me.dto.request.mail.GetOtpRequest;
import org.ping_me.dto.request.mail.OtpVerificationRequest;
import org.ping_me.dto.response.mail.GetOtpResponse;
import org.ping_me.dto.response.mail.OtpVerificationResponse;
import org.ping_me.service.mail.OtpService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

/**
 * @author : user664dntp
 * @mailto : phatdang19052004@gmail.com
 * @created : 18/01/2026, Sunday
 **/
@RestController
@RequestMapping("/otp")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class OtpController {

    OtpService otpService;

    TurnstileClient turnstileClient;

    @Value("${cloudflare.turnstile.secret-key}")
    @NonFinal
    String secretKey;

    @GetMapping("/admin/status")
    ApiResponse<Boolean> isAdminVerified() {
        return ApiResponse.<Boolean>builder()
                .errorCode(HttpStatus.OK.value())
                .errorMessage(HttpStatus.OK.name())
                .data(otpService.checkAdminIsVerified())
                .build();
    }

    @PostMapping("/send")
    ApiResponse<GetOtpResponse> sendOtp(@RequestBody GetOtpRequest request) {
        validateTurnstile(request.getTurnstileToken());

        GetOtpResponse res = otpService.sendOtp(request);
        HttpStatus httpStatus = res.getIsSent() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ApiResponse.<GetOtpResponse>builder()
                .errorCode(httpStatus.value())
                .errorMessage(httpStatus.name())
                .data(res)
                .build();
    }

    @PostMapping("/verify")
    ApiResponse<OtpVerificationResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
        return ApiResponse.<OtpVerificationResponse>builder()
                .errorCode(HttpStatus.OK.value())
                .errorMessage(HttpStatus.OK.name())
                .data(otpService.verifyOtp(request))
                .build();
    }

    public void validateTurnstile(String token) {
        TurnstileResponse response = turnstileClient
                .verifyToken(secretKey, token);

        if (!response.success()) {
            String errors = String.join(",", response.errorCodes());
            throw new AccessDeniedException(errors);
        }
    }
}
