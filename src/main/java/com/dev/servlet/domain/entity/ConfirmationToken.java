package com.dev.servlet.domain.entity;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Where;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_confirmation_token")
@ToString
@Where(clause = "used = false AND expires_at > CURRENT_TIMESTAMP")
public class ConfirmationToken {
    @Id
    @Column(name = "id", updatable = false)
    private UUID id;
    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name = "created_at")
    private OffsetDateTime createdAt;
    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;
    @Column(name = "used")
    private boolean used;
    @Column(name = "body", columnDefinition = "TEXT")
    private String body;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UuidCreator.getTimeOrdered();
    }
}
