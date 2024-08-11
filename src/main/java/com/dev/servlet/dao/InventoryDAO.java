package com.dev.servlet.dao;

import com.dev.servlet.domain.Inventory;
import com.dev.servlet.domain.enums.StatusEnum;
import com.dev.servlet.utils.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.List;

public class InventoryDAO extends BaseDAO<Inventory, Long> {

    public InventoryDAO(EntityManager em) {
        super(em, Inventory.class);
    }

    /**
     * Find one
     *
     * @param inventory
     * @return {@link Inventory}
     */
    @Override
    public Inventory find(Inventory inventory) {
        List<Inventory> all = findAll(inventory);
        if (CollectionUtils.isNullOrEmpty(all)) {
            return null;
        }
        return all.get(0);
    }

    /**
     * Find all
     *
     * @param inventory
     * @return {@link List}
     */
    @Override
    public List<Inventory> findAll(Inventory inventory) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
        Root<Inventory> root = cq.from(Inventory.class);

        Predicate predicate = cb.equal(root.get("status"), StatusEnum.ACTIVE.getName());
        predicate = cb.and(predicate, cb.equal(root.get("user").get("id"), inventory.getUser().getId()));

        if (inventory.getDescription() != null) {
            Expression<String> upper = cb.upper(root.get("description"));
            Predicate like = cb.like(upper, inventory.getDescription().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }

        if (inventory.getProduct() != null) {
            Expression<String> upper = cb.upper(root.get("product").get("name"));
            Predicate pProduct = cb.like(upper, inventory.getProduct().getName().toUpperCase() + "%");
            if (inventory.getProduct().getCategory() != null) {
                Predicate pCategory = cb.equal(root.get("product").get("category").get("id"), inventory.getProduct().getCategory().getId());
                pProduct = cb.and(pProduct, pCategory);
            }

            predicate = cb.and(predicate, pProduct);
        }

        Order desc = cb.desc(root.get("id"));
        cq.select(root).where(predicate).orderBy(desc);

        List<Inventory> inventories = em.createQuery(cq).getResultList();
        if (CollectionUtils.isNullOrEmpty(inventories)) {
            return Collections.emptyList();
        }

        return inventories;
    }

    /**
     * Delete one
     *
     * @param inventory
     */
    @Override
    public void delete(Inventory inventory) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaUpdate<Inventory> cu = builder.createCriteriaUpdate(Inventory.class);
        Root<Inventory> root = cu.from(Inventory.class);
        cu.set("status", StatusEnum.DELETED.getName());
        Predicate predicate = builder.equal(root.get("id"), inventory.getId());
        cu.where(predicate);
        Query query = em.createQuery(cu);
        int update = query.executeUpdate();
    }

    /**
     * Check if product has inventory
     *
     * @param product
     * @return boolean
     */
    public boolean hasInventory(Inventory inventory) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Inventory> root = cq.from(Inventory.class);

        Predicate predicate = cb.equal(root.get("status"), StatusEnum.ACTIVE.getName());
        predicate = cb.and(predicate, cb.equal(root.get("product").get("id"), inventory.getProduct().getId()));

        cq.select(cb.count(root)).where(predicate);

        Long count = em.createQuery(cq).getSingleResult();
        return count > 0;
    }
}
