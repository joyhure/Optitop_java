package com.optitop.optitop_api.dto;

import com.optitop.optitop_api.model.User.Role;
import java.time.LocalDateTime;

public class UserDisplayDTO {
    private Integer id;
    private String login;
    private Role role;
    private String lastname;
    private String firstname;
    private String email;
    private LocalDateTime createdAt;

    public UserDisplayDTO(
            Integer id,
            String login,
            Role role,
            String lastname,
            String firstname,
            String email,
            LocalDateTime createdAt) {
        this.id = id;
        this.login = login;
        this.role = role;
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Role getRole() {
        return role;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}