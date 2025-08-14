package me.huynhducphu.PingMe_Backend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.huynhducphu.PingMe_Backend.dto.request.user.CreateUserRequest;
import me.huynhducphu.PingMe_Backend.dto.response.user.DefaultUserResponse;
import me.huynhducphu.PingMe_Backend.model.User;
import me.huynhducphu.PingMe_Backend.model.constant.AuthProvider;
import me.huynhducphu.PingMe_Backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Admin 8/3/2025
 **/
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements me.huynhducphu.PingMe_Backend.service.UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public DefaultUserResponse saveUser(CreateUserRequest createUserRequest) {
        if (userRepository.existsByEmail(createUserRequest.getEmail()))
            throw new DataIntegrityViolationException("Email đã tồn tại");

        var user = modelMapper.map(createUserRequest, User.class);
        user.setAuthProvider(AuthProvider.LOCAL);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        var savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, DefaultUserResponse.class);
    }

    @Override
    public Page<DefaultUserResponse> getAllUsers(Pageable pageable) {
        return userRepository
                .findAll(pageable)
                .map(user -> modelMapper.map(user, DefaultUserResponse.class));
    }

    @Override
    public DefaultUserResponse getUserById(Long id) {
        return userRepository
                .findById(id)
                .map(user -> modelMapper.map(user, DefaultUserResponse.class))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng với id này"));
    }


}
