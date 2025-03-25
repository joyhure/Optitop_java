package com.optitop.optitop_api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.optitop.optitop_api.model.User.Role;

@Entity
@Table(name = "pending_accounts")
public class PendingAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String lastname;
    private String firstname;
    private String email;
    private String login;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id", nullable = true)
    private User createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructeurs
    public PendingAccount() {
    }

    public PendingAccount(String lastname, String firstname, String email, String login, Role role, User createdBy) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.login = login;
        this.role = role;
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
