package com.hackaton.auth.user.dto;

import java.util.List;

public record UserPage(List<UserResponse> items, long total) {
}
