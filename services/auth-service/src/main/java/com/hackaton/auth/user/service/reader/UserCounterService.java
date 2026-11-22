package com.hackaton.auth.user.service.reader;

import com.hackaton.auth.user.dto.UserCounts;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserCounterService implements UserCounter {

    private final UserAccountRepository userRepository;

    @Override
    public UserCounts count() {
        return new UserCounts(userRepository.count(), userRepository.countByEnabled(true),
                userRepository.countByEnabled(false), userRepository.countByRole(Role.ADMIN));
    }
}
