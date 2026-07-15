package com.kole.platform.identity.domain.model;

import com.kole.platform.common.persistence.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "users",
    schema = "identity"
)
public class User extends AuditableEntity {

    @Column(
        name = "full_name",
        nullable = false,
        length = 150
    )
    private String fullName;

    @Column(
        name = "email",
        nullable = false,
        unique = true,
        length = 254
    )
    private String email;

    @Column(
        name = "phone_number",
        nullable = false,
        unique = true,
        length = 30
    )
    private String phoneNumber;

    @Column(
        name = "password_hash",
        nullable = false,
        length = 255
    )
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "status",
        nullable = false,
        length = 40
    )
    private UserStatus status;

    @Column(
        name = "email_verified",
        nullable = false
    )
    private boolean emailVerified;

    @Column(
        name = "phone_verified",
        nullable = false
    )
    private boolean phoneVerified;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        schema = "identity",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    protected User() {
    }

    public User(
        String fullName,
        String email,
        String phoneNumber,
        String passwordHash,
        UserStatus status
    ) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.status = status;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void recordSuccessfulLogin(Instant loginTime) {
        lastLoginAt = loginTime;
    }

    public boolean canAuthenticate() {
        return status == UserStatus.ACTIVE
            || status == UserStatus.PENDING_VERIFICATION;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserStatus getStatus() {
        return status;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}