package me.huynhducphu.PingMe_Backend.advice;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import me.huynhducphu.PingMe_Backend.dto.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

/**
 * Admin 7/31/2025
 **/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
            SQLIntegrityConstraintViolationException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleConflictDataExceptions(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupportedException(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(new ApiResponse<>("API không hỗ trợ phương thức này"));
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);

        String errorMessage = e
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(errorMessage));
    }

    @ExceptionHandler(value = {
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequestExceptions(Exception e) {
        log.error(e.getMessage(), e);

        String errorMessage = "Tham số không hợp lệ";
        if (e instanceof MethodArgumentTypeMismatchException ex) {
            Class<?> type = ex.getRequiredType();
            String typeName = type != null ? type.getSimpleName() : "valid type";
            errorMessage = ex.getName() + " phải là " + typeName;
        }


        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(errorMessage));
    }

    @ExceptionHandler(value = {
            BadCredentialsException.class,
            UsernameNotFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(Exception e) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("Tài khoản hoặc mật khẩu không hợp lệ"));
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthorizationException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(e.getMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(e.getMessage()));
    }


}
