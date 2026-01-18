//package me.huynhducphu.ping_me.service.mail.impl;
//
//import lombok.AccessLevel;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import lombok.experimental.NonFinal;
//import me.huynhducphu.ping_me.dto.base.ApiResponse;
//import me.huynhducphu.ping_me.dto.request.mail.OtpVerificationRequest;
//import me.huynhducphu.ping_me.dto.request.mail.SendOtpRequest;
//import me.huynhducphu.ping_me.dto.response.mail.OtpVerificationResponse;
//import me.huynhducphu.ping_me.service.mail.MailClient;
//import me.huynhducphu.ping_me.service.mail.MailService;
//import me.huynhducphu.ping_me.service.redis.RedisService;
//import me.huynhducphu.ping_me.utils.OtpGenerator;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author : user664dntp
// * @mailto : phatdang19052004@gmail.com
// * @created : 18/01/2026, Sunday
// **/
//@RequiredArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//@Service
//public class MailServiceImpl implements MailService {
//    RedisService redisService;
//    MailClient mailClient;
//
//    @NonFinal
//    @Value("${spring.mail.timeout}")
//    long timeout;
//
//    @Override
//    public OtpVerificationResponse sendMailToAdmin(OtpVerificationRequest request) {
//        String otp = OtpGenerator.generateOtp(6);
//        redisService.set("otp-" + request.getEmail(), otp, timeout, TimeUnit.MINUTES);
//        try {
//            ApiResponse<Boolean> res = mailClient.sendOtpToAdmin(
//                    SendOtpRequest.builder()
//                            .toMail(request.getEmail())
//                            .otp(otp)
//                            .build()
//            );
//            return OtpVerificationResponse.builder()
//                    .mailRecipient(request.getEmail())
//                    .otp(otp)
//                    .isValid(res.getData())
//                    .build();
//        }catch (Exception e){
//            throw new IllegalArgumentException("Failed to send OTP email to admin.");
//        }
//    }
//}
