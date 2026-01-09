package me.huynhducphu.ping_me.utils.mapper;

import lombok.RequiredArgsConstructor;
import me.huynhducphu.ping_me.dto.response.authentication.CurrentUserSessionResponse;
import me.huynhducphu.ping_me.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

/**
 * Admin 1/9/2026
 *
 **/
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final ModelMapper modelMapper;

    public CurrentUserSessionResponse mapToCurrentUserSessionResponse(User user) {
        var res = modelMapper.map(user, CurrentUserSessionResponse.class);

        var roleName = user.getRole() != null ? user.getRole().getName() : "";
        res.setRoleName(roleName);
        return res;
    }

}
