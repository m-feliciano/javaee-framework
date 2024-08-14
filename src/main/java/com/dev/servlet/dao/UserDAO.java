package com.dev.servlet.dao;

import com.dev.servlet.domain.User;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.utils.CollectionUtils;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class UserDAO extends BaseDAO<User, Long> {

    public UserDAO() {
        super(User.class);
    }

    /**
     * Find all
     *
     * @param user
     * @return {@link List}
     */
    public List<User> findAll(User user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class).distinct(true);
        Root<User> root = cq.from(User.class);
        Predicate predicate = cb.notEqual(root.get("status"), StatusEnum.DELETED.getName());
        predicate = cb.and(predicate, cb.equal(root.get("login"), user.getLogin()));
        predicate = cb.and(predicate, cb.equal(root.get("password"), user.getPassword()));

        Order descId = cb.desc(root.get("id"));
        cq.select(root).where(predicate).orderBy(descId);

        List<User> resultList = em.createQuery(cq).getResultList();
        return resultList;
    }

    /**
     * Find one
     *
     * @param user
     * @return {@link User}
     */
    public User find(User user) {
        List<User> all = findAll(user);
        if (CollectionUtils.isNullOrEmpty(all)) {
            return null;
        }
        return all.get(0);
    }

    /**
     * Delete one
     *
     * @param user
     */
    public void delete(User user) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<User> cu = builder.createCriteriaUpdate(User.class);
        Root<User> root = cu.from(User.class);
        cu.set("status", StatusEnum.DELETED.getName());
        Predicate predicate = builder.equal(root.get("id"), user.getId());
        cu.where(predicate);
        Query query = em.createQuery(cu);
        int update = query.executeUpdate();
    }
}
