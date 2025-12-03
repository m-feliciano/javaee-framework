package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.port.out.activity.UserActivityLogRepositoryPort;
import com.dev.servlet.domain.entity.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@RequestScoped
public class UserActivityLogRepository extends BaseRepository<UserActivityLog, String> implements UserActivityLogRepositoryPort {

    @Override
    public Collection<UserActivityLog> findAll(UserActivityLog object) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserActivityLog> cq = cb.createQuery(UserActivityLog.class);
        Root<UserActivityLog> root = cq.from(UserActivityLog.class);
        Predicate predicate = buildDefaultPredicateFor(object, cb, root);

        cq.where(predicate).orderBy(cb.desc(root.get("timestamp")));
        TypedQuery<UserActivityLog> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Override
    protected Predicate buildDefaultPredicateFor(UserActivityLog filter, CriteriaBuilder cb, Root<?> root) {
        Predicate predicate = cb.conjunction();
        if (filter.getUserId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("userId"), filter.getUserId()));
        }

        if (filter.getAction() != null) {
            Expression<String> upper = cb.upper(root.get("action"));
            Predicate like = cb.like(upper, "%" + filter.getAction().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }
        if (filter.getHttpStatusCode() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("httpStatusCode"), filter.getHttpStatusCode()));
        }
        if (filter.getStatus() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("status"), filter.getStatus()));
        }

        return predicate;
    }

    public List<UserActivityLog> findByUserIdAndDateRange(String userId, Date startDate, Date endDate, String status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UserActivityLog> cq = cb.createQuery(UserActivityLog.class);
        Root<UserActivityLog> root = cq.from(UserActivityLog.class);
        Predicate predicate = cb.equal(root.get("userId"), userId);

        if (StringUtils.isNotBlank(status)) {
            predicate = cb.and(predicate, cb.equal(root.get("status"), status.toUpperCase()));
        }
        if (startDate != null) {
            predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("timestamp"), startDate));
        }
        if (endDate != null) {
            predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("timestamp"), endDate));
        }

        cq.where(predicate).orderBy(cb.desc(root.get("timestamp")));
        TypedQuery<UserActivityLog> query = em.createQuery(cq);
        return query.getResultList();
    }
}
