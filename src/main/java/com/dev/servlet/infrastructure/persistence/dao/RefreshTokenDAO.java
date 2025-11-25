package com.dev.servlet.infrastructure.persistence.dao;

import com.dev.servlet.domain.model.RefreshToken;
import com.dev.servlet.infrastructure.persistence.dao.base.BaseDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.util.Optional;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class RefreshTokenDAO extends BaseDAO<RefreshToken, String> {

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
        Session session = openSession();
        session.createQuery(query)
                .setParameter("userId", userId)
                .executeUpdate();
        session.getTransaction().commit();
    }
}
