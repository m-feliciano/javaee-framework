package com.dev.servlet.infrastructure.persistence.repository;

import com.dev.servlet.application.exception.AppException;
import com.dev.servlet.application.port.out.category.CategoryRepositoryPort;
import com.dev.servlet.domain.entity.Category;
import com.dev.servlet.domain.entity.enums.Status;
import com.dev.servlet.infrastructure.persistence.repository.base.BaseRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import java.sql.Statement;
import java.util.Collections;
import java.util.List;

@Slf4j
@NoArgsConstructor
@RequestScoped
public class CategoryRepository extends BaseRepository<Category, String> implements CategoryRepositoryPort {

    @Override
    public List<Category> findAll(Category category) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> cq = cb.createQuery(Category.class);
        Root<Category> root = cq.from(Category.class);
        Predicate predicate = cb.equal(root.get(STATUS), Status.ACTIVE.getValue());
        predicate = cb.and(predicate, cb.equal(root.get(USER).get("id"), category.getUser().getId()));
        if (category.getName() != null) {
            Expression<String> upper = cb.upper(root.get("name"));
            Predicate like = cb.like(upper, "%" + category.getName().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }
        Order desc = cb.asc(root.get(ID));
        cq.select(root).where(predicate).orderBy(desc);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public void delete(Category category) {
        executeInTransaction(() -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Category> cu = cb.createCriteriaUpdate(Category.class);
            Root<Category> root = cu.from(Category.class);

            cu.set(STATUS, Status.DELETED.getValue());
            Predicate predicate = cb.equal(root.get(ID), category.getId());
            predicate = cb.and(predicate,
                    cb.equal(root.get(USER).get(ID), category.getUser().getId()));
            cu.where(predicate);

            em.createQuery(cu).executeUpdate();
            return null;
        });
    }

    @Override
    public List<Category> saveAll(List<Category> categories) throws AppException {
        Session session = em.unwrap(Session.class);
        beginTransaction();

        session.doWork(connection -> {
            String copies = String.join(", ", Collections.nCopies(3, "?"));
            String sql = "INSERT INTO tb_category (name, status, user_id) VALUES (" + copies + ")";
            try (var ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                for (Category category : categories) {
                    ps.setString(1, category.getName());
                    ps.setString(2, Status.ACTIVE.getValue());
                    ps.setString(3, category.getUser().getId());
                    ps.addBatch();
                }
                ps.executeBatch();
                try (var rs = ps.getGeneratedKeys()) {
                    int i = 0;
                    while (rs.next()) {
                        categories.get(i).setId(rs.getString(1));
                        i++;
                    }
                }
            }
        });

        commitTransaction(true);

        return categories;
    }

    @Override
    public void updateName(Category category) {
        executeInTransaction(() -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaUpdate<Category> cu = cb.createCriteriaUpdate(Category.class);
            Root<Category> root = cu.from(Category.class);

            cu.set("name", category.getName());
            Predicate predicate = cb.equal(root.get(ID), category.getId());
            cu.where(predicate);

            em.createQuery(cu).executeUpdate();
            return null;
        });
    }

    @Override
    protected Predicate buildDefaultPredicateFor(Category filter, CriteriaBuilder cb, Root<?> root) {
        Predicate predicate = cb.equal(root.get(STATUS), Status.ACTIVE.getValue());
        predicate = cb.and(predicate, cb.equal(root.get(USER).get("id"), filter.getUser().getId()));
        if (filter.getName() != null) {
            Expression<String> upper = cb.upper(root.get("name"));
            Predicate like = cb.like(upper, "%" + filter.getName().toUpperCase() + "%");
            predicate = cb.and(predicate, like);
        }

        if (filter.getId() != null) {
            predicate = cb.and(predicate, cb.equal(root.get(ID), filter.getId()));
        }
        return predicate;
    }
}
