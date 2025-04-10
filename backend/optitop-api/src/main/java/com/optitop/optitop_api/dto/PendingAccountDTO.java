package com.optitop.optitop_api.dto;

import com.optitop.optitop_api.model.User.Role;
import jakarta.validation.constraints.*;

public record PendingAccountDTO(
        String lastname,
        String firstname,
        String email,
        @NotBlank(message = "L'identifiant est obligatoire") String login,
        Role role,
        @NotBlank(message = "Le type de demande est obligatoire") String requestType) {
}