package com.dev.servlet.infrastructure.persistence.repository.base;

import com.dev.servlet.application.mapper.Mapper;
import com.dev.servlet.infrastructure.persistence.transfer.IPageRequest;
import com.dev.servlet.infrastructure.persistence.transfer.IPageable;
import com.dev.servlet.infrastructure.persistence.transfer.internal.PageResponse;
import com.dev.servlet.shared.util.ClassUtil;
import com.dev.servlet.shared.vo.Sort;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@NoArgsConstructor
public abstract class BaseRepository<T, ID> implements BaseRepositoryPort<T, ID> {
    protected static final String STATUS = "status";
    protected static final String USER = "user";
    protected static final String ID = "id";
    protected EntityManager em;
    private Class<T> specialization;

    public abstract Collection<T> findAll(T object);

    protected Predicate buildDefaultPredicateFor(T filter, CriteriaBuilder cb, Root<?> root) {
        return cb.conjunction();
    }

    @Inject
    public void setEm(EntityManager em) {
        this.em = em;
    }

    @PostConstruct
    public void init() {
        specialization = ClassUtil.getSubClassType(this.getClass());
    }

    public Optional<T> findById(ID id) {
        T value = em.find(specialization, id);
        return Optional.ofNullable(value);
    }

    @Override
    public List<T> saveAll(List<T> entities) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Optional<T> find(T filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(specialization);
        Root<T> root = query.from(specialization);
        Predicate predicate = buildDefaultPredicateFor(filter, cb, root);
        query.where(predicate).select(root);
        TypedQuery<T> typedQuery = em.createQuery(query);
        return typedQuery.getResultStream().findFirst();
    }

    public T save(T object) {
        return executeInTransaction(() -> {
            em.persist(object);
            return object;
        });
    }

    protected <R> R executeInTransaction(TransactionAction<R> action) {
        try {
            beginTransaction();
            R result = action.execute();
            commitTransaction();
            return result;
        } catch (Exception e) {
            log.error("Transaction failed: {}", e.getMessage());
            rollbackTransaction();
            throw new RuntimeException("Transaction failed", e);
        }
    }

    public T update(T object) {
        return executeInTransaction(() -> em.merge(object));
    }

    public void delete(T object) {
        executeInTransaction(() -> {
            em.remove(object);
            return null;
        });
    }

    private void beginTransaction() {
        if (!em.getTransaction().isActive()) {
            em.getTransaction().begin();
        }
    }

    /**
     * Commits the current transaction and clears the EntityManager to detach all managed entities.
     * In case of an exception during commit, it rolls back the transaction.
     * Note: Do not attempt to use the EntityManager after this method is called, as it will be cleared.
     *
     * @throws RuntimeException if the commit fails
     */
    protected void commitTransaction() {
        try {
            em.flush(); // Flush changes to the database
            em.getTransaction().commit(); // Commit the transaction
            em.clear(); // Detach all managed entities
        } catch (Exception e) {
            log.error("Error committing transaction: {}", e.getMessage());
            rollbackTransaction();
            throw e;
        }
    }

    protected void rollbackTransaction() {
        try {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } catch (Exception e) {
            log.error("Error rolling back transaction: {}", e.getMessage());
        }
    }

    public IPageable<T> getAllPageable(IPageRequest pageRequest) {
        log.debug("BaseRepository: fetching pageable data: type={}, page={}, size={}, sort={}",
                specialization.getSimpleName(),
                pageRequest.getInitialPage(),
                pageRequest.getPageSize(),
                pageRequest.getSort().getField() + ":" + pageRequest.getSort().getDirection());

        StopWatch sw = new StopWatch();
        sw.start();
        try {
            long totalCount = count(pageRequest);
            List<T> resultSet = Collections.emptyList();

            if (totalCount > pageRequest.getFirstResult()) {
                resultSet = getPage(pageRequest);
            }

            return PageResponse.<T>builder()
                    .content(resultSet)
                    .totalElements(totalCount)
                    .currentPage(pageRequest.getInitialPage())
                    .pageSize(pageRequest.getPageSize())
                    .sort(pageRequest.getSort())
                    .build();
        } finally {
            sw.stop();
            log.debug("BaseRepository: finished fetching pageable data in {} ms", sw.getTime());
        }
    }

    public <U> IPageable<U> getAllPageable(IPageRequest pageRequest, Mapper<T, U> mapper) {
        IPageable<T> page = getAllPageable(pageRequest);
        var content = page.getContent().stream().map(mapper::map).toList();
        return PageResponse.<U>builder()
                .content(content)
                .totalElements(page.getTotalElements())
                .currentPage(page.getCurrentPage())
                .pageSize(page.getPageSize())
                .sort(page.getSort())
                .build();
    }

    @SuppressWarnings("unchecked")
    private List<T> getPage(IPageRequest pageRequest) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(specialization);
        Root<T> root = query.from(specialization);
        if (pageRequest.getSort() != null && pageRequest.getSort().getField() != null) {
            Path<Object> path = root.get(pageRequest.getSort().getField());
            query.orderBy(pageRequest.getSort().getDirection() == Sort.Direction.DESC ? cb.desc(path) : cb.asc(path));
        }
        Predicate predicate = buildDefaultPredicateFor((T) pageRequest.getFilter(), cb, root);
        query.where(predicate).select(root).distinct(true);
        TypedQuery<T> typedQuery = em.createQuery(query)
                .setFirstResult(pageRequest.getFirstResult())
                .setMaxResults(pageRequest.getPageSize());
        return typedQuery.getResultList();
    }

    @FunctionalInterface
    protected interface TransactionAction<R> {
        R execute();
    }

    @SuppressWarnings("unchecked")
    public long count(IPageRequest pageRequest) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(specialization);
        Predicate predicate = buildDefaultPredicateFor((T) pageRequest.getFilter(), cb, root);
        query.where(predicate).select(cb.count(root));
        TypedQuery<Long> typedQuery = em.createQuery(query);
        Long count = typedQuery.getSingleResult();
        return count != null ? count : 0L;
    }
}
