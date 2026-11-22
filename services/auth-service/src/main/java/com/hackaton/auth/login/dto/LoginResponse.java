package com.hackaton.auth.login.dto;

import com.hackaton.auth.user.entity.Role;
import java.util.List;

public record LoginResponse(String token, String email, String fullName, Role role, List<Role> roles) {
}
