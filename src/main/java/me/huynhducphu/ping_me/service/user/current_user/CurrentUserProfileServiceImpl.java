package me.huynhducphu.ping_me.service.user.current_user;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.request.authentication.ChangePasswordRequest;
import me.huynhducphu.ping_me.dto.request.authentication.ChangeProfileRequest;
import me.huynhducphu.ping_me.dto.response.authentication.CurrentUserProfileResponse;
import me.huynhducphu.ping_me.dto.response.authentication.CurrentUserSessionResponse;
import me.huynhducphu.ping_me.repository.jpa.auth.UserRepository;
import me.huynhducphu.ping_me.service.s3.S3Service;
import me.huynhducphu.ping_me.service.user.CurrentUserProfileService;
import me.huynhducphu.ping_me.service.user.CurrentUserProvider;
import me.huynhducphu.ping_me.utils.mapper.UserMapper;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Admin 1/9/2026
 *
 **/
@Service
@RequiredArgsConstructor
@Transactional
public class CurrentUserProfileServiceImpl implements CurrentUserProfileService {

    private final PasswordEncoder passwordEncoder;

    private final S3Service s3Service;

    private final UserMapper userMapper;
    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    private final CurrentUserProvider currentUserProvider;
    private static final Long MAX_AVATAR_FILE_SIZE = 2 * 1024 * 1024L;

    @Override
    public CurrentUserProfileResponse getCurrentUserInfo() {
        var user = currentUserProvider.get();
        var currentUserProfileResponse = modelMapper.map(user, CurrentUserProfileResponse.class);

        String roleName = user.getRole() != null ? user.getRole().getName() : "";
        currentUserProfileResponse.setRoleName(roleName);
        return currentUserProfileResponse;
    }

    @Override
    public CurrentUserSessionResponse getCurrentUserSession() {
        return userMapper.mapToCurrentUserSessionResponse(currentUserProvider.get());
    }

    @Override
    public CurrentUserSessionResponse updateCurrentUserPassword(
            ChangePasswordRequest changePasswordRequest
    ) {
        var user = currentUserProvider.get();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword()))
            throw new DataIntegrityViolationException("Mật khẩu cũ không chính xác");

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));

        return userMapper.mapToCurrentUserSessionResponse(user);
    }

    @Override
    public CurrentUserSessionResponse updateCurrentUserProfile(
            ChangeProfileRequest changeProfileRequest
    ) {
        var user = currentUserProvider.get();

        user.setName(changeProfileRequest.getName());
        user.setGender(changeProfileRequest.getGender());
        user.setAddress(changeProfileRequest.getAddress());
        user.setDob(changeProfileRequest.getDob());

        return userMapper.mapToCurrentUserSessionResponse(user);
    }

    @Override
    public CurrentUserSessionResponse updateCurrentUserAvatar(
            MultipartFile avatarFile
    ) {
        var user = currentUserProvider.get();

        String url = s3Service.uploadFile(
                avatarFile,
                "avatar",
                user.getEmail(),
                true,
                MAX_AVATAR_FILE_SIZE
        );

        user.setAvatarUrl(url);
        user.setUpdatedAt(LocalDateTime.now());

        return userMapper.mapToCurrentUserSessionResponse(user);
    }

    @Override
    public void connect(Long userId) {
        userRepository.connect(userId);
    }

    @Override
    public void disconnect(Long userId) {
        userRepository.disconnect(userId);
    }

}
