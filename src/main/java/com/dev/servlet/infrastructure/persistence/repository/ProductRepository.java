package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.exception.ApplicationException;
import com.dev.servlet.application.port.out.product.ProductRepositoryPort;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.Product;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepository;
import com.dev.servlet.shared.util.CollectionUtils;
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
import java.sql.Statement;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class ProductRepository extends BaseRepository<Product, String> implements ProductRepositoryPort {

    private Predicate buildDefaultFilter(Product product, CriteriaBuilder criteriaBuilder, Root<Product> root) {
        Predicate predicate = criteriaBuilder.notEqual(root.get(STATUS), Status.DELETED.getValue());

        if (product.getUser() != null) {
            predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get(USER).get("id"), product.getUser().getId()));
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
            predicate = builder.and(predicate, builder.equal(root.get(USER).get(ID), product.getUser().getId()));
            cu.where(predicate);

            Query query = em.createQuery(cu);
            query.executeUpdate();
            return null;
        });
    }

    @Override
    public List<Product> saveAll(List<Product> products) throws ApplicationException {
        AtomicReference<String> errors = new AtomicReference<>();
        Session session = em.unwrap(Session.class);
        session.getTransaction().begin();

        session.doWork(connection -> {
            String copies = String.join(", ", Collections.nCopies(9, "?"));
            String sql = "INSERT INTO tb_product (id, name, description, url_img, register_date, price, user_id, status, category_id) VALUES (" + copies + ")";
            try (var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Product product : products) {
                    ps.setString(1, UUID.randomUUID().toString());
                    ps.setString(2, product.getName());
                    ps.setString(3, product.getDescription());
                    ps.setString(4, product.getUrl());
                    ps.setDate(5, java.sql.Date.valueOf(product.getRegisterDate()));
                    ps.setBigDecimal(6, product.getPrice());
                    ps.setString(7, product.getUser().getId());
                    ps.setString(8, Status.ACTIVE.getValue());
                    if (product.getCategory() != null) {
                        ps.setString(9, product.getCategory().getId());
                    } else {
                        ps.setNull(9, java.sql.Types.BIGINT);
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                try (var rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next()) {
                        products.get(i).setId(rs.getString(1));
                        i++;
                    }
                }
            } catch (Exception e) {
                errors.set(e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            }
        });

        if (errors.get() != null) {
            throw new ApplicationException(errors.get());
        }

        try {
            session.getTransaction().commit();
        } catch (Exception e) {
            session.getTransaction().rollback();
        }

        return products;
    }

    public BigDecimal calculateTotalPriceFor(Product filter) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);
        Root<Product> root = query.from(Product.class);
        Predicate predicate = buildDefaultFilter(filter, builder, root);

        query.where(predicate).select(builder.sum(root.get("price")));
        BigDecimal totalPrice = em.createQuery(query).getSingleResult();
        return ObjectUtils.getIfNull(totalPrice, BigDecimal.ZERO);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Predicate buildDefaultPredicateFor(Product filter, CriteriaBuilder cb, Root<?> root) {
        return buildDefaultFilter(filter, cb, (Root<Product>) root);
    }
}
