package com.optitop.optitop_api.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {
    @NotBlank(message = "L'identifiant est obligatoire")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String password;

    // Getters et Setters
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
