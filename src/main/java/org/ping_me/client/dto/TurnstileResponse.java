package org.ping_me.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Admin 3/3/2026
 *
 **/
public record TurnstileResponse(
        boolean success,
        @JsonProperty("error-codes")
        List<String> errorCodes,
        String challenge_ts,
        String hostname
) {
}
