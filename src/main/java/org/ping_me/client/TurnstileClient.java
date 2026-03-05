package org.ping_me.client;

import org.ping_me.client.dto.TurnstileResponse;
import org.ping_me.config.feign.MailFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;

/**
 * Admin 3/3/2026
 *
 **/
@FeignClient(
        name = "cloudflare-turnstile-service",
        url = "${cloudflare.turnstile.verify-url}",
        configuration = MailFeignConfig.class
)
public interface TurnstileClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TurnstileResponse verifyToken(@RequestPart("secret") String secret,
                                  @RequestPart("response") String response);

}
