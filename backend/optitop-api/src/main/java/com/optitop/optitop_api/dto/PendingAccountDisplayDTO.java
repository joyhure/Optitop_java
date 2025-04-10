package com.optitop.optitop_api.dto;

import com.optitop.optitop_api.model.User.Role;
import java.time.LocalDateTime;

public record PendingAccountDisplayDTO(
        String lastname,
        String firstname,
        String email,
        String login,
        Role role,
        String requestType,
        LocalDateTime createdAt,
        String createdByLogin) {
}