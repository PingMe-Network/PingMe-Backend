package org.ping_me.service.user;

import org.ping_me.model.constant.UserStatus;

public interface CurrentUserProfileService {
    void updateStatus(Long userId, UserStatus status);
}
