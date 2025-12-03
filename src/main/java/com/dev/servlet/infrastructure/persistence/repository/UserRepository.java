package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.port.out.user.UserRepositoryPort;
import com.dev.servlet.domain.entity.User;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;

import java.util.List;

@RequestScoped
@NoArgsConstructor
public class UserRepository extends BaseRepository<User, String> implements UserRepositoryPort {

    public static final String CREDENTIALS = "credentials";

    @Override
    public List<User> findAll(User user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class).distinct(true);
        Root<User> root = cq.from(User.class);
        Predicate predicate = buildDefaultPredicateFor(user, cb, root);
        Order descId = cb.asc(root.get(ID));
        cq.select(root).where(predicate).orderBy(descId);
        TypedQuery<User> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public void delete(User user) {
        executeInTransaction(() -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<User> cu = cb.createCriteriaUpdate(User.class);
            Root<User> root = cu.from(User.class);

            cu.set(STATUS, Status.DELETED.getValue());
            cu.where(cb.equal(root.get(ID), user.getId()));

            em.createQuery(cu).executeUpdate();
            return null;
        });
    }

    @Override
    protected Predicate buildDefaultPredicateFor(User filter, CriteriaBuilder cb, Root<?> root) {
        Predicate predicate = cb.or(
                cb.equal(root.get(STATUS), Status.ACTIVE.getValue()),
                cb.equal(root.get(STATUS), Status.PENDING.getValue())
        );

        if (filter.getCredentials() != null) {
            if (filter.getCredentials().getLogin() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get(CREDENTIALS).get("login"), filter.getCredentials().getLogin()));
            }
            if (filter.getCredentials().getPassword() != null) {
                predicate = cb.and(predicate,
                        cb.equal(root.get(CREDENTIALS).get("password"), filter.getCredentials().getPassword()));
            }
        }
        return predicate;
    }
}
