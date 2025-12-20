package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.inventory.InventoryRepositoryPort;
import com.dev.servlet.domain.entity.Inventory;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepository;
import com.dev.servlet.shared.util.CollectionUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class InventoryRepository extends BaseRepository<Inventory, String> implements InventoryRepositoryPort {
    public static final String PRODUCT = "product";

    @Override
    public List<Inventory> findAll(Inventory inventory) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Inventory> cq = cb.createQuery(Inventory.class);
        Root<Inventory> root = cq.from(Inventory.class);
        Predicate predicate = cb.equal(root.get(STATUS), Status.ACTIVE.getValue());
        predicate = cb.and(predicate, cb.equal(root.get(USER).get(ID), inventory.getUser().getId()));
        if (inventory.getId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get(ID), inventory.getId()));
        } else if (inventory.getDescription() != null) {
            Expression<String> upper = cb.upper(root.get("description"));
            Predicate like = cb.like(upper, "%" + inventory.getDescription().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }
        if (inventory.getProduct() != null) {
            Predicate pProduct = cb.conjunction();
            if (inventory.getProduct().getId() != null) {
                pProduct = cb.equal(root.get(PRODUCT).get(ID), inventory.getProduct().getId());
            } else {
                if (inventory.getProduct().getName() != null) {
                    Expression<String> upper = cb.upper(root.get(PRODUCT).get("name"));
                    pProduct = cb.and(pProduct, cb.like(upper, "%" + inventory.getProduct().getName().toUpperCase() + "%"));
                }
                if (inventory.getProduct().getCategory() != null) {
                    Predicate pCategory = cb.equal(root.get(PRODUCT).get("category").get(ID), inventory.getProduct().getCategory().getId());
                    pProduct = cb.and(pProduct, pCategory);
                }
            }
            predicate = cb.and(predicate, pProduct);
        }
        Order desc = cb.asc(root.get(ID));
        cq.select(root).where(predicate).orderBy(desc);
        TypedQuery<Inventory> typedQuery = em.createQuery(cq);
        List<Inventory> inventories = typedQuery.getResultList();
        if (CollectionUtils.isEmpty(inventories)) {
            return Collections.emptyList();
        }
        return inventories;
    }

    @Override
    public void delete(Inventory inventory) {
        executeInTransaction(() -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Inventory> cu = cb.createCriteriaUpdate(Inventory.class);
            Root<Inventory> root = cu.from(Inventory.class);

            cu.set(STATUS, Status.DELETED.getValue());
            Predicate predicate = cb.equal(root.get(ID), inventory.getId());
            predicate = cb.and(predicate,
                    cb.equal(root.get(USER).get(ID), inventory.getUser().getId()));
            cu.where(predicate);
            em.createQuery(cu).executeUpdate();
            return null;
        });
    }

    public boolean has(Inventory inventory) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Inventory> root = cq.from(Inventory.class);
        Join<Inventory, Product> joinProduct = root.join(PRODUCT, JoinType.LEFT);
        Join<Inventory, Product> joinUser = root.join(USER, JoinType.LEFT);
        Predicate predicate = cb.equal(root.get(STATUS), Status.ACTIVE.getValue());
        predicate = cb.and(predicate,
                cb.equal(joinProduct.get(ID), inventory.getProduct().getId()),
                cb.equal(joinUser.get(ID), inventory.getUser().getId()));
        cq.select(cb.count(root)).where(predicate);
        Long count = em.createQuery(cq).getSingleResult();
        return count > 0;
    }

    @Override
    public List<Inventory> saveAll(List<Inventory> inventories) throws AppException {
        Session session = em.unwrap(Session.class);
        beginTransaction();

        session.doWork(connection -> {
            String copies = String.join(", ", Collections.nCopies(5, "?"));
            String sql = "INSERT INTO tb_inventory (id, description, product_id, user_id, status) VALUES (" + copies + ")";
            try (java.sql.PreparedStatement ps = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                for (Inventory inventory : inventories) {
                    ps.setString(1, inventory.getId());
                    ps.setString(2, inventory.getDescription());
                    ps.setString(3, inventory.getProduct().getId());
                    ps.setString(4, inventory.getUser().getId());
                    ps.setString(5, Status.ACTIVE.getValue());
                    ps.addBatch();
                }
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next()) {
                        inventories.get(i).setId(rs.getString(1));
                        i++;
                    }
                }
                ps.executeBatch();
            }
        });

        try {
            commitTransaction(true);
        } catch (Exception e) {
            log.error("Error committing transaction for saving inventories", e);
            throw new AppException("Failed to save inventories");
        }

        return inventories;
    }
    @Override
    protected Predicate buildDefaultPredicateFor(Inventory filter, CriteriaBuilder cb, Root<?> root) {
        Predicate predicate = cb.equal(root.get(STATUS), Status.ACTIVE.getValue());
        predicate = cb.and(predicate, cb.equal(root.get(USER).get(ID), filter.getUser().getId()));

        // Inventory filtering
        if (filter.getId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get(ID), filter.getId()));
        }

        if (filter.getUser() != null) {
            predicate = cb.and(predicate, cb.equal(root.get(USER).get(ID), filter.getUser().getId()));
        }

        if (filter.getDescription() != null) {
            Expression<String> upper = cb.upper(root.get("description"));
            Predicate like = cb.like(upper, "%" + filter.getDescription().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }

        // Product filtering
        if (filter.getProduct() != null) {
            Predicate pProduct = cb.conjunction();
            if (filter.getProduct().getId() != null) {
                pProduct = cb.equal(root.get(PRODUCT).get(ID), filter.getProduct().getId());
            } else {
                if (filter.getProduct().getCategory() != null) {
                    Predicate pCategory = cb.equal(root.get(PRODUCT).get("category").get(ID), filter.getProduct().getCategory().getId());
                    pProduct = cb.and(pProduct, pCategory);
                }
            }
            predicate = cb.and(predicate, pProduct);
        }

        return predicate;
    }
}
