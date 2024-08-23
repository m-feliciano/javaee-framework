package com.dev.servlet.dao;

import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.utils.CollectionUtils;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class CategoryDAO extends BaseDAO<Category, Long> {

    public CategoryDAO() {
        super(Category.class);
    }

    /**
     * Find all
     *
     * @param category
     * @return {@link List}
     */
    public List<Category> findAll(Category category) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);

        Predicate predicate = cb.equal(root.get("status"), StatusEnum.ACTIVE.getName());
        predicate = cb.and(predicate, cb.equal(root.get("user"), category.getUser()));

        if (category.getName() != null) {
            Expression<String> upper = cb.upper(root.get("name"));
            Predicate like = cb.like(upper, category.getName().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }

        Order desc = cb.desc(root.get("id"));
        cq.select(root).where(predicate).orderBy(desc);

        List<Category> categories = em.createQuery(cq).getResultList();
        return categories;
    }

    /**
     * Find one
     *
     * @param category
     * @return {@link Category}
     */
    public Category find(Category category) {
        List<Category> all = findAll(category);
        if (CollectionUtils.isNullOrEmpty(all)) {
            return null;
        }
        return all.get(0);
    }

    /**
     * Delete one
     *
     * @param category
     */
    public void delete(Category category) {
        beginTransaction();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<Category> cu = builder.createCriteriaUpdate(Category.class);
        Root<Category> root = cu.from(Category.class);
        cu.set("status", StatusEnum.DELETED.getName());
        Predicate predicate = builder.equal(root.get("id"), category.getId());
        cu.where(predicate);
        Query query = em.createQuery(cu);
        int update = query.executeUpdate();
        commitTransaction();
    }
}
