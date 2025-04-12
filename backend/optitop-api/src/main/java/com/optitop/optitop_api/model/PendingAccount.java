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

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "login", nullable = false, length = 50, unique = true)
    private String login;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false)
    private RequestType requestType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_pending_account_created_by"), nullable = false)
    private User createdBy;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    public enum RequestType {
        ajout, modification, suppression
    }

    // Constructeurs

    // Constructeur par défaut requis par JPA
    public PendingAccount() {
    }

    public PendingAccount(String lastname, String firstname, String email,
            String login, Role role, User createdBy, RequestType requestType) {
        this.lastname = lastname;
        this.firstname = firstname;
        this.email = email;
        this.login = login;
        this.role = role;
        this.createdBy = createdBy;
        this.requestType = requestType;
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

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
