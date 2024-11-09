package com.dev.servlet.dao;

import com.dev.servlet.pojo.Category;
import com.dev.servlet.pojo.Product;
import com.dev.servlet.pojo.enums.StatusEnum;
import com.dev.servlet.pojo.records.Order;
import com.dev.servlet.pojo.records.Pagination;
import com.dev.servlet.utils.CollectionUtils;

import javax.enterprise.inject.Model;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

@Model
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

        Predicate predicate = getDefaultPredicate(product, cb, root);

        javax.persistence.criteria.Order descId = cb.desc(root.get("id"));
        query.where(predicate).select(root).orderBy(descId);

        List<Product> resultList = em.createQuery(query).getResultList();
        if (!CollectionUtils.isNullOrEmpty(resultList)) {
            return resultList;
        }

        return Collections.emptyList();
    }

    /**
     * Return the predicate to be used in the query
     *
     * @param product
     * @param cb
     * @param root
     * @return
     */
    private static Predicate getDefaultPredicate(Product product, CriteriaBuilder cb, Root<Product> root) {
        Predicate predicate = cb.notEqual(root.get("status"), StatusEnum.DELETED.value);

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
                Join<Product, Category> join = root.join("category", javax.persistence.criteria.JoinType.LEFT);

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
        return predicate;
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
        cu.set("status", StatusEnum.DELETED.value);

        Predicate predicate = builder.equal(root.get("id"), product.getId());
        predicate = builder.and(predicate,
                builder.equal(root.get("user").get("id"), product.getUser().getId()));

        cu.where(predicate);

        javax.persistence.Query query = em.createQuery(cu);
        query.executeUpdate();
        commitTransaction();
    }

    /**
     * Find all products using query
     *
     * @param product
     * @param pagination
     * @return
     */
    public List<Product> findAll(Product product, Pagination pagination) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> cQuery = cb.createQuery(Product.class).distinct(true);
        Root<Product> root = cQuery.from(Product.class);

        Predicate predicate = getDefaultPredicate(product, cb, root);

        Path<Object> path = root.get(pagination.getSort().getValue());
        if (Order.DESC.equals(pagination.getOrder())) {
            cQuery.orderBy(cb.desc(path));
        } else {
            cQuery.orderBy(cb.asc(path));
        }

        cQuery.where(predicate).select(root);

        List<Product> resultList = em.createQuery(cQuery)
                .setFirstResult(pagination.getFirstResult())
                .setMaxResults(pagination.getPageSize())
                .getResultList();

        if (!CollectionUtils.isNullOrEmpty(resultList)) {
            return resultList;
        }

        return Collections.emptyList();
    }

    /**
     * Count the number of products
     *
     * @param product
     * @return
     */
    public Long getTotalResults(Product product) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Product> root = query.from(Product.class);

        Predicate predicate = getDefaultPredicate(product, cb, root);

        query.where(predicate).select(cb.count(root));

        return em.createQuery(query).getSingleResult();
    }
}
