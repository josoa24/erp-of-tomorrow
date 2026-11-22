package com.hackaton.auth.user.service.password;

import com.hackaton.auth.common.exception.BusinessRuleException;
import com.hackaton.auth.common.exception.ResourceNotFoundException;
import com.hackaton.auth.user.dto.PasswordChangeRequest;
import com.hackaton.auth.user.entity.UserAccount;
import com.hackaton.auth.user.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PasswordChangerService implements PasswordChanger {

    private final UserAccountRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void change(Long userId, PasswordChangeRequest request) {
        UserAccount user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Compte", userId));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("Le mot de passe actuel est incorrect");
        }
        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessRuleException("Le nouveau mot de passe doit être différent de l'actuel");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    }
}
