package me.huynhducphu.PingMe_Backend.dto.base;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin 8/3/2025
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiResponse<T> {

    private String errorMessage;
    private Integer errorCode;
    private T data;

    public ApiResponse(String errorMessage) {
        this.errorMessage = errorMessage;
        this.errorCode = null;
        this.data = null;
    }

    public ApiResponse(String errorMessage, Integer errorCode) {
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
