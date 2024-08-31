package com.dev.servlet.dao;

import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.utils.CollectionUtils;

import javax.enterprise.inject.Model;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Model
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

        Predicate predicate = cb.equal(root.get("status"), StatusEnum.ACTIVE.value);
        predicate = cb.and(predicate, cb.equal(root.get("user"), category.getUser()));

        if (category.getName() != null) {
            Expression<String> upper = cb.upper(root.get("name"));
            Predicate like = cb.like(upper, category.getName().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }

        Order desc = cb.desc(root.get("id"));
        cq.select(root).where(predicate).orderBy(desc);

        return em.createQuery(cq).getResultList();
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
        beginTransaction();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<Category> cu = builder.createCriteriaUpdate(Category.class);
        Root<Category> root = cu.from(Category.class);
        cu.set("status", StatusEnum.DELETED.value);

        Predicate predicate = builder.equal(root.get("id"), category.getId());
        predicate = builder.and(predicate,
                builder.equal(root.get("user").get("id"), category.getUser().getId()));

        cu.where(predicate);
        Query query = em.createQuery(cu);
        query.executeUpdate();
        commitTransaction();
    }
}
