package me.huynhducphu.PingMe_Backend.advice.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * Admin 8/16/2025
 **/
@Data
public class S3UploadException extends RuntimeException {
    private HttpStatus httpStatus;

    public S3UploadException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
