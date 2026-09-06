package com.david.monitoring.auth.dto;

public record AuthResponse(
        Long userId,
        String username,
        String email
) {}
