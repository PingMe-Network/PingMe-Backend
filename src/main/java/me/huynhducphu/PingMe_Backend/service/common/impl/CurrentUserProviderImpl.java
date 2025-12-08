package me.huynhducphu.PingMe_Backend.service.common.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.repository.auth.UserRepository;
import me.huynhducphu.PingMe_Backend.service.common.CurrentUserProvider;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Admin 8/19/2025
 **/
@Component
@RequiredArgsConstructor
public class CurrentUserProviderImpl implements CurrentUserProvider {
    private final UserRepository userRepository;

    @Override
    public User get() {
        // Lấy email của người dùng hiện tại từ SecurityContext.
        // Đây là nơi Spring Security giữ thông tin đăng nhập sau khi xác thực.
        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // ===================================================================================================
        // THÔNG TIN BỔ SUNG
        //
        // Khi gửi request lên, client luôn phải kèm theo JWT trong header.
        // Bên trong JWT có chứa email và một số thông tin cơ bản khác.
        // Spring Security sẽ tự động tách JWT, kiểm tra và lưu thông tin vào SecurityContextHolder.
        // ===================================================================================================

        // Từ email đã lấy được, truy vấn xuống database để tìm user tương ứng.
        // Nếu không tìm thấy, ném ra ngoại lệ báo rằng không có người dùng nào khớp.
        return userRepository
                .getUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng hiện tại"));
    }
}
