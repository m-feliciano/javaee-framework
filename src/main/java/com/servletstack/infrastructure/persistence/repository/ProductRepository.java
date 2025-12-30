package com.servletstack.infrastructure.persistence.repository;

import com.servletstack.application.port.out.product.ProductRepositoryPort;
import com.servletstack.domain.entity.Category;
import com.servletstack.domain.entity.Product;
import com.servletstack.domain.entity.enums.Status;
import com.servletstack.infrastructure.persistence.repository.base.BaseRepository;
import com.servletstack.shared.util.CollectionUtils;
import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.Session;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class ProductRepository extends BaseRepository<Product, UUID> implements ProductRepositoryPort {

    private Predicate buildDefaultFilter(Product product, CriteriaBuilder criteriaBuilder, Root<Product> root) {
        Predicate predicate = criteriaBuilder.notEqual(root.get(STATUS), Status.DELETED.getValue());

        if (product.getOwner() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("owner").get("id"), product.getOwner().getId()));
        }

        if (product.getId() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(ID), product.getId()));

        } else {
            if (product.getName() != null) {
                Expression<String> upper = criteriaBuilder.upper(root.get("name"));
                Predicate like = criteriaBuilder.like(upper, product.getName().toUpperCase() + "%");
                predicate = criteriaBuilder.and(predicate, like);
            }

            if (product.getDescription() != null) {
                Expression<String> upper = criteriaBuilder.upper(root.get("description"));
                Predicate like = criteriaBuilder.like(upper, "%" + product.getDescription().toUpperCase() + "%");
                predicate = criteriaBuilder.and(predicate, like);
            }

            if (product.getCategory() != null) {
                Join<Product, Category> join = root.join("category", JoinType.INNER);
                if (product.getCategory().getId() != null) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(join.get(ID), product.getCategory().getId()));
                } else {
                    if (product.getCategory().getName() != null) {
                        Expression<String> upper = criteriaBuilder.upper(join.get("name"));
                        Predicate like = criteriaBuilder.like(upper, "" + product.getCategory().getName().toUpperCase() + "%");
                        predicate = criteriaBuilder.and(predicate, like);
                    }
                }
            }
        }

        return predicate;
    }

    @Override
    public List<Product> findAll(Product product) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class).distinct(true);
        Root<Product> root = query.from(Product.class);
        Predicate predicate = buildDefaultFilter(product, cb, root);

        jakarta.persistence.criteria.Order descId = cb.asc(root.get(ID));
        query.where(predicate).select(root).orderBy(descId);
        TypedQuery<Product> typedQuery = em.createQuery(query);

        List<Product> resultList = typedQuery.getResultList();
        if (!CollectionUtils.isEmpty(resultList)) {
            return resultList;
        }

        return Collections.emptyList();
    }

    @Override
    public void delete(Product product) {
        executeInTransaction(() -> {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaUpdate<Product> cu = builder.createCriteriaUpdate(Product.class);
            Root<Product> root = cu.from(Product.class);
            cu.set(STATUS, Status.DELETED.getValue());

            Predicate predicate = builder.equal(root.get(ID), product.getId());
            predicate = builder.and(predicate, builder.equal(root.get("owner").get(ID), product.getOwner().getId()));
            cu.where(predicate);

            Query query = em.createQuery(cu);
            query.executeUpdate();
            return null;
        });
    }

    @Override
    public List<Product> saveAll(List<Product> products) {
        Session session = em.unwrap(Session.class);
        session.doWork(conn -> {
            try {
                conn.setAutoCommit(false);
                insertProducts(conn, products);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw new RuntimeException("Failed to save products", e);
            }
        });

        closeEm();
        return products;
    }

    public BigDecimal calculateTotalPriceFor(Product filter) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);
        Root<Product> root = query.from(Product.class);
        Predicate predicate = buildDefaultFilter(filter, builder, root);

        query.where(predicate).select(builder.sum(root.get("price")));
        BigDecimal totalPrice = em.createQuery(query).getSingleResultOrNull();
        return ObjectUtils.getIfNull(totalPrice, BigDecimal.ZERO);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Predicate buildDefaultPredicateFor(Product filter, CriteriaBuilder cb, Root<?> root) {
        return buildDefaultFilter(filter, cb, (Root<Product>) root);
    }

    private void insertProducts(Connection connection, List<Product> products) throws SQLException {
        String sql = """
                INSERT INTO tb_product
                (id, name, description, register_date, price, user_id, status, category_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (Product product : products) {
                product.setId(UuidCreator.getTimeOrdered());

                ps.setObject(1, product.getId());
                ps.setString(2, product.getName());
                ps.setString(3, product.getDescription());
                ps.setDate(4, Date.valueOf(product.getRegisterDate()));
                ps.setBigDecimal(5, product.getPrice());
                ps.setObject(6, product.getOwner().getId());
                ps.setString(7, Status.ACTIVE.getValue());
                ps.setObject(8, product.getCategory());
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
}
