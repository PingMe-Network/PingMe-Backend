package me.huynhducphu.PingMe_Backend.dto.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApiResponse<T> {

    private String errorMessage;
    private String errorCode;
    private T data;

    public ApiResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = null;
        this.data = null;
    }

    public ApiResponse(String errorMessage, String errorCode) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.data = null;
    }

    public ApiResponse(T data) {
        this.data = data;
        this.errorMessage = null;
        this.errorCode = null;
    }
}
