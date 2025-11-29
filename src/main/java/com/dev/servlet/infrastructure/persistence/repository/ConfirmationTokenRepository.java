package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.port.out.repository.ConfirmationTokenRepositoryPort;
import com.dev.servlet.domain.entity.ConfirmationToken;
import jakarta.enterprise.inject.Model;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Model
public class ConfirmationTokenRepository extends BaseRepository<ConfirmationToken, String> implements ConfirmationTokenRepositoryPort {

    @Override
    public Collection<ConfirmationToken> findAll(ConfirmationToken object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ConfirmationToken> saveAll(List<ConfirmationToken> entities) {
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
