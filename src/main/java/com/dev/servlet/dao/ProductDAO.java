package com.dev.servlet.dao;

import com.dev.servlet.domain.Category;
import com.dev.servlet.domain.Product;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.utils.CollectionUtils;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

public class ProductDAO extends BaseDAO<Product, Long> {

    public ProductDAO() {
        super(Product.class);
    }

    /**
     * Find one
     *
     * @param product
     * @return {@link List}
     */
    @Override
    public Product find(Product product) {
        List<Product> all = findAll(product);
        if (CollectionUtils.isNullOrEmpty(all)) {
            return null;
        }
        return all.get(0);
    }

    /**
     * Find all by user/product
     *
     * @param product
     * @return {@link List}
     */
    public List<Product> findAll(Product product) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class).distinct(true);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = cb.notEqual(root.get("status"), StatusEnum.DELETED.getName());

        if (product.getUser() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("user"), product.getUser()));
        }

        if (product.getId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get("id"), product.getId()));

        } else {

            if (product.getName() != null) {
                Expression<String> upper = cb.upper(root.get("name"));
                Predicate like = cb.like(upper, product.getName().toUpperCase() + "%");
                predicate = cb.and(predicate, like);
            }

            if (product.getDescription() != null) {
                Expression<String> upper = cb.upper(root.get("description"));
                Predicate like = cb.like(upper, product.getDescription().toUpperCase() + "%");
                predicate = cb.and(predicate, like);
            }

            if (product.getCategory() != null) {
                Join<Product, Category> join = root.join("category");

                if (product.getCategory().getId() != null) {
                    cb.equal(join.get("id"), product.getCategory().getId());
                } else {
                    if (product.getCategory().getName() != null) {
                        Expression<String> upper = cb.upper(join.get("name"));
                        Predicate like = cb.like(upper, product.getCategory().getName().toUpperCase() + "%");
                        predicate = cb.and(predicate, like);
                    }
                }
            }
        }

        Order descId = cb.desc(root.get("id"));
        query.where(predicate).select(root).orderBy(descId);

        List<Product> resultList = em.createQuery(query).getResultList();
        if (!CollectionUtils.isNullOrEmpty(resultList)) {
            return resultList;
        }

        return Collections.emptyList();
    }

    /**
     * Delete one
     *
     * @param product
     */
    @Override
    public void delete(Product product) {
        beginTransaction();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<Product> cu = builder.createCriteriaUpdate(Product.class);
        Root<Product> root = cu.from(Product.class);
        cu.set("status", StatusEnum.DELETED.getName());
        cu.where(builder.equal(root.get("id"), product.getId()));
        Query query = em.createQuery(cu);
        int update = query.executeUpdate();
        commitTransaction();
    }

}
