package com.hackaton.auth.user.service.password;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.PasswordResetRequest;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordResetterService implements PasswordResetter {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void reset(Long targetId, PasswordResetRequest request, Long actorId) {
        if (targetId.equals(actorId)) {
            throw new BusinessRuleException("Utilisez le changement de mot de passe de votre propre compte");
        }
        UserAccount user = userRepository.findById(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte", targetId));
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }
}
