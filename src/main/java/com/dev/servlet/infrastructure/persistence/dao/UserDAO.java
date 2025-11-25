package com.dev.servlet.infrastructure.persistence.dao;

import com.dev.servlet.core.util.CollectionUtils;
import com.dev.servlet.domain.model.User;
import com.dev.servlet.domain.model.enums.Status;
import com.dev.servlet.infrastructure.persistence.dao.base.BaseDAO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@RequestScoped
public class UserDAO extends BaseDAO<User, String> {
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
    public Optional<User> find(User user) {
        List<User> all = findAll(user);
        if (CollectionUtils.isEmpty(all)) {
            return Optional.empty();
        }
        return Optional.ofNullable(all.getFirst());
    }

    @Override
    public void delete(User user) {
        Session session = openSession();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<User> cu = builder.createCriteriaUpdate(User.class);
        Root<User> root = cu.from(User.class);
        cu.set(STATUS, Status.DELETED.getValue());
        Predicate predicate = builder.equal(root.get(ID), user.getId());
        cu.where(predicate);
        Query query = em.createQuery(cu);
        query.executeUpdate();
        session.getTransaction().commit();
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
