package com.dev.servlet.domain.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@jakarta.persistence.Entity
@Table(name = "tb_refresh_token")
public class RefreshToken implements Serializable {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;
    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "revoked", nullable = false)
    private boolean revoked;
    @Column(name = "issued_at")
    private Instant issuedAt;
    @Column(name = "expires_at")
    private Instant expiresAt;
    @Column(name = "replaced_by")
    private UUID replacedBy;
    @Column(name = "ip_address")
    private String ipAddress;
    @Column(name = "user_agent")
    private String userAgent;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
    }
}
