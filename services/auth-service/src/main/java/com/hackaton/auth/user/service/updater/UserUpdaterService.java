package com.hackaton.auth.user.service.updater;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.UserResponse;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.mapper.UserMapper;
import com.hackaton.auth.user.repository.UserAccountRepository;
import java.util.Set;
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
    public UserResponse changeRoles(Long id, Set<Role> roles, Long actorId) {
        UserAccount user = findOther(id, actorId, "modifier les rôles de");
        if (!roles.contains(Role.ADMIN)) {
            guardLastAdmin(user, "retirer le rôle Administrateur du");
        }
        user.setRoles(roles);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse setEnabled(Long id, boolean enabled, Long actorId) {
        UserAccount user = findOther(id, actorId, enabled ? "réactiver" : "désactiver");
        if (!enabled) {
            guardLastAdmin(user, "désactiver le");
        }
        user.setEnabled(enabled);
        return userMapper.toResponse(user);
    }

    private void guardLastAdmin(UserAccount user, String action) {
        if (user.isEnabled() && user.getRole() == Role.ADMIN
                && userRepository.lockEnabledByRole(Role.ADMIN).size() <= 1) {
            throw new BusinessRuleException("Impossible de " + action + " dernier compte administrateur actif");
        }
    }

    private UserAccount findOther(Long id, Long actorId, String action) {
        if (id.equals(actorId)) {
            throw new BusinessRuleException("Impossible de " + action + " son propre compte");
        }
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Compte", id));
    }
}
