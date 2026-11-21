package com.hackaton.auth.common.config;

import com.hackaton.auth.user.dto.UserCreateRequest;
import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.repository.UserAccountRepository;
import com.hackaton.auth.user.service.creator.UserCreator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserAccountRepository userRepository;
    private final UserCreator userCreator;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userCreator.create(new UserCreateRequest(adminEmail, adminPassword, Role.ADMIN));
        }
    }
}
