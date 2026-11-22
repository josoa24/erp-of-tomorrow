package com.hackaton.auth.user.service.updater;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUpdaterService implements UserUpdater {

    private final UserAccountRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse changeRole(Long id, Role role, Long actorId) {
        UserAccount user = findOther(id, actorId, "modifier le rôle de");
        user.setRole(role);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse setEnabled(Long id, boolean enabled, Long actorId) {
        UserAccount user = findOther(id, actorId, enabled ? "réactiver" : "désactiver");
        user.setEnabled(enabled);
        return userMapper.toResponse(user);
    }

    private UserAccount findOther(Long id, Long actorId, String action) {
        if (id.equals(actorId)) {
            throw new BusinessRuleException("Impossible de " + action + " son propre compte");
        }
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compte", id));
    }
}
