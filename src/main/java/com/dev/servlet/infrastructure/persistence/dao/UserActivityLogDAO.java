package com.dev.servlet.infrastructure.persistence.dao;

import com.dev.servlet.domain.model.UserActivityLog;
import com.dev.servlet.infrastructure.persistence.dao.base.BaseDAO;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.MatchMode;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@RequestScoped
public class UserActivityLogDAO extends BaseDAO<UserActivityLog, String> {

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
            Predicate like = cb.like(upper, MatchMode.ANYWHERE.toMatchString(filter.getAction().toUpperCase()));
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

