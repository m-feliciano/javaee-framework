package com.servletstack.infrastructure.persistence.repository;

import com.servletstack.application.port.out.confirmtoken.ConfirmationTokenRepositoryPort;
import com.servletstack.domain.entity.ConfirmationToken;
import com.servletstack.infrastructure.persistence.repository.base.BaseRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestScoped
public class ConfirmationTokenRepository extends BaseRepository<ConfirmationToken, UUID> implements ConfirmationTokenRepositoryPort {

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
        return results.isEmpty() ? Optional.empty() : Optional.of(results.getFirst());
    }

    @Override
    public boolean existsValidTokenForUser(UUID userId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<ConfirmationToken> root = query.from(ConfirmationToken.class);
        query.select(cb.count(root));
        query.where(
                cb.and(
                        cb.equal(root.get("userId"), userId),
                        cb.greaterThan(root.get("expiresAt"), cb.currentTimestamp()),
                        cb.equal(root.get("used"), false)
                )
        );

        Long count = em.createQuery(query).getSingleResultOrNull();
        return count != null && count > 0;
    }
}
