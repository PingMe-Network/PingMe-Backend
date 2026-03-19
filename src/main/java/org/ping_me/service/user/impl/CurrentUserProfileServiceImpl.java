package org.ping_me.service.user.impl;

import lombok.RequiredArgsConstructor;
import org.ping_me.model.constant.UserStatus;
import org.ping_me.repository.jpa.auth.UserRepository;
import org.ping_me.service.user.CurrentUserProfileService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserProfileServiceImpl implements CurrentUserProfileService {

    private final UserRepository userRepository;

    @Override
    public void updateStatus(Long userId, UserStatus status) {
        userRepository.updateStatus(userId, status);
    }
}
