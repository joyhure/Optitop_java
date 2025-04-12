package com.optitop.optitop_api.dto;

import com.optitop.optitop_api.model.User.Role;
import java.time.LocalDateTime;

public class PendingAccountDisplayDTO {
        private Integer id;
        private String lastname;
        private String firstname;
        private String email;
        private String login;
        private Role role;
        private String requestType;
        private LocalDateTime createdAt;
        private String createdByLogin;

        public PendingAccountDisplayDTO(
                        Integer id,
                        String lastname,
                        String firstname,
                        String email,
                        String login,
                        Role role,
                        String requestType,
                        LocalDateTime createdAt,
                        String createdByLogin) {
                this.id = id;
                this.lastname = lastname;
                this.firstname = firstname;
                this.email = email;
                this.login = login;
                this.role = role;
                this.requestType = requestType;
                this.createdAt = createdAt;
                this.createdByLogin = createdByLogin;
        }

        // Getter pour id
        public Integer getId() {
                return id;
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

        public String getLogin() {
                return login;
        }

        public Role getRole() {
                return role;
        }

        public String getRequestType() {
                return requestType;
        }

        public LocalDateTime getCreatedAt() {
                return createdAt;
        }

        public String getCreatedByLogin() {
                return createdByLogin;
        }
}