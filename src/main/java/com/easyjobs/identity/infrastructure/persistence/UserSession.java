package com.easyjobs.identity.infrastructure.persistence;

import java.time.OffsetDateTime;
import java.util.UUID;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_sessions")
public class UserSession extends PanacheEntityBase {

    @Id
    @Column(name = "id", nullable = false)
    public UUID id;

    @Column(name = "user_id", nullable = false)
    public UUID userId;

    @Column(name = "access_token", nullable = false)
    public String accessToken;

    @Column(name = "refresh_token", nullable = false)
    public String refreshToken;

    @Column(name = "expires_at", nullable = false)
    public OffsetDateTime expiresAt;

    @Column(name = "created_at", nullable = false)
    public OffsetDateTime createdAt;

    public UserSession() {
        // JPA constructor
    }

    public UserSession(
            UUID id,
            UUID userId,
            String accessToken,
            String refreshToken,
            OffsetDateTime expiresAt,
            OffsetDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }
}