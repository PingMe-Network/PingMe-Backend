//package me.huynhducphu.ping_me.controller.mail;
//
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import me.huynhducphu.ping_me.dto.base.ApiResponse;
//import me.huynhducphu.ping_me.dto.request.mail.OtpVerificationRequest;
//import me.huynhducphu.ping_me.dto.response.mail.OtpVerificationResponse;
//import me.huynhducphu.ping_me.service.mail.MailService;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author : user664dntp
// * @mailto : phatdang19052004@gmail.com
// * @created : 18/01/2026, Sunday
// **/
//@RestController
//@RequestMapping("mail-management/api/v1/mails")
//@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
//@RequiredArgsConstructor
//public class MailController {
//    MailService mailService;
//
//    @GetMapping("/admin-verification")
//    ApiResponse<OtpVerificationResponse> sendOtpToAdmin(@RequestBody OtpVerificationRequest request) {
//        OtpVerificationResponse res = mailService.sendMailToAdmin(request);
//        HttpStatus httpStatus = res.getIsValid() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
//        return ApiResponse.<OtpVerificationResponse>builder()
//                .errorCode(httpStatus.value())
//                .errorMessage(httpStatus.name())
//                .data(res)
//                .build();
//    }
//}
