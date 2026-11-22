package com.hackaton.auth.common.config;

import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.repository.UserAccountRepository;
import com.hackaton.auth.user.service.creator.UserCreator;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Component
public class AdminSeeder implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final UserCreator userCreator;
    private final String adminEmail;
    private final String adminPassword;
    private final String adminName;

    public AdminSeeder(UserAccountRepository userRepository, UserCreator userCreator,
                       @Value("${admin.email}") String adminEmail,
                       @Value("${admin.password}") String adminPassword,
                       @Value("${admin.name}") String adminName) {
        this.userRepository = userRepository;
        this.userCreator = userCreator;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminName = adminName;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userCreator.create(new UserCreateRequest(adminEmail, adminPassword, List.of(Role.ADMIN), null, adminName));
            return;
        }
        completeMissingName();
    }

    private void completeMissingName() {
        if (!StringUtils.hasText(adminName)) {
            return;
        }
        userRepository.findByEmail(adminEmail)
                .filter(admin -> admin.getFullName() == null)
                .ifPresent(admin -> admin.setFullName(adminName.strip()));
    }
}
