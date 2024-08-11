package com.dev.servlet.dao;

import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.utils.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.List;

public class CategoryDAO extends BaseDAO<Category, Long> {

    public CategoryDAO(EntityManager em) {
        super(em, Category.class);
    }

    /**
     * Find all
     *
     * @param category
     * @return {@link List}
     */
    @Override
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
    @Override
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
    @Override
    public void delete(Category category) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<Category> cu = builder.createCriteriaUpdate(Category.class);
        Root<Category> root = cu.from(Category.class);
        cu.set("status", StatusEnum.DELETED.getName());
        Predicate predicate = builder.equal(root.get("id"), category.getId());
        cu.where(predicate);
        Query query = em.createQuery(cu);
        int update = query.executeUpdate();
    }
}
