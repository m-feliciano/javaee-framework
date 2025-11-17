package com.dev.servlet.infrastructure.persistence.dao;

import com.dev.servlet.domain.model.RefreshToken;
import com.dev.servlet.infrastructure.persistence.dao.base.BaseDAO;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
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

    @Override
    protected Predicate buildDefaultPredicateFor(RefreshToken filter, CriteriaBuilder cb, Root<?> root) {
        return cb.conjunction();
    }
}
