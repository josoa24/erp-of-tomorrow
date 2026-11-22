package com.hackaton.auth.user.controller;

import com.hackaton.auth.user.dto.UserCounts;
import com.hackaton.auth.user.service.reader.UserCounter;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserCountsController {

    private final UserCounter userCounter;

    @Operation(summary = "Compteurs des comptes : total, actifs, désactivés, administrateurs")
    @GetMapping("/users/counts")
    public UserCounts counts() {
        return userCounter.count();
    }
}
