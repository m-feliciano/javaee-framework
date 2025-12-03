package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.port.out.refreshtoken.RefreshTokenRepositoryPort;
import com.dev.servlet.domain.entity.RefreshToken;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class RefreshTokenRepository extends BaseRepository<RefreshToken, String> implements RefreshTokenRepositoryPort {

    public Optional<RefreshToken> findByToken(String token) {
        String query = "SELECT r FROM RefreshToken r WHERE r.token = :token AND revoked = false";
        TypedQuery<RefreshToken> q = em.createQuery(query, RefreshToken.class);
        q.setParameter("token", token);
        RefreshToken result = q.getSingleResult();
        return Optional.ofNullable(result);
    }

    @Override
    public java.util.Collection<RefreshToken> findAll(RefreshToken object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void revokeAll(String userId) {
        String query = "UPDATE RefreshToken r SET r.revoked = true WHERE r.user.id = :userId AND r.revoked = false";

        executeInTransaction(() -> {
            em.createQuery(query)
                    .setParameter("userId", userId)
                    .executeUpdate();
            return null;
        });
    }
}
