package com.dev.servlet.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_confirmation_token")
@ToString
public class ConfirmationToken {
    @Id
    @Column(name = "id", updatable = false)
    @GeneratedValue(generator = "short-uuid")
    @GenericGenerator(name = "short-uuid", strategy = "com.dev.servlet.core.util.ShortUuidGenerator")
    private String id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "used")
    private boolean used;

    @Column(name = "body", columnDefinition = "TEXT")
    private String body;
}

