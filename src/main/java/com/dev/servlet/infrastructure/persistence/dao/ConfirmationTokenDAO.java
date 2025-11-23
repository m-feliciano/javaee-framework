package com.dev.servlet.infrastructure.persistence.dao;

import com.dev.servlet.domain.model.ConfirmationToken;
import com.dev.servlet.infrastructure.persistence.dao.base.BaseDAO;

import javax.enterprise.inject.Model;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Model
public class ConfirmationTokenDAO extends BaseDAO<ConfirmationToken, String> {
    @Override
    public Collection<ConfirmationToken> findAll(ConfirmationToken object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Optional<ConfirmationToken> findByToken(String token) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ConfirmationToken> query = cb.createQuery(ConfirmationToken.class);
        Root<ConfirmationToken> root = query.from(ConfirmationToken.class);
        query.where(cb.equal(root.get("token"), token));
        TypedQuery<ConfirmationToken> typedQuery = em.createQuery(query);
        List<ConfirmationToken> results = typedQuery.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}

