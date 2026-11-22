package com.hackaton.auth.user.service.creator;

import com.hackaton.auth.common.exception.DuplicateResourceException;
import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class UserCreatorService implements UserCreator {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("Un compte avec l'email " + request.email() + " existe déjà");
        }
        UserAccount user = new UserAccount();
        user.setEmail(request.email());
        user.setFullName(StringUtils.hasText(request.fullName()) ? request.fullName().strip() : null);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        return userMapper.toResponse(userRepository.save(user));
    }
}
