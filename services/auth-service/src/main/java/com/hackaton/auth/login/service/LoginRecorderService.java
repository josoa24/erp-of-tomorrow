package com.hackaton.auth.login.service;

import com.hackaton.auth.user.repository.UserAccountRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginRecorderService implements LoginRecorder {

    private final UserAccountRepository userRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(Long userId) {
        userRepository.updateLastLoginAt(userId, Instant.now());
    }
}
