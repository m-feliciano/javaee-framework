package dao;

import domain.Category;
import domain.Product;
import domain.User;
import domain.enums.Status;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class ProductDao extends BaseDao {

    public static final String STATUS = "status";
    public static final String ID = "id";
    public static final String USER = "user";
    public static final String NAME = "name";
    public static final String DESCRIPTION = "description";
    public static final String CATEGORY = "category";

    public ProductDao(EntityManager em) {
        super(em);
    }

    /**
     * Save.
     *
     * @param product the product
     * @return the product saved
     */

    public Product save(Product product) {
        beginTransaction();
        product.setStatus(Status.ACTIVE.getDescription());
        this.em.persist(product);
        commitTransaction();
        closeTransaction();
        return product;
    }

    /**
     * Update.
     *
     * @param product the prod
     */

    public void update(Product product) {
        beginTransaction();
        if (product.getStatus() == null) {
            product.setStatus(Status.ACTIVE.getDescription());
        }
        this.em.merge(product);
        commitTransaction();
        closeTransaction();
    }

    /**
     * delete by id.
     *
     * @param product the product
     * @return true if deleted, false if not found
     */

    public boolean delete(Product product) {
        Product prod = this.find(product);
        boolean deleted = false;
        if (prod != null) {
            beginTransaction();
            prod = em.merge(prod);
            prod.setStatus(Status.DELETED.getDescription());
            commitTransaction();
            deleted = true;
        }
        closeTransaction();
        return deleted;
    }

    /**
     * Find by id.
     *
     * @param product the product
     * @return product with the given id
     */

    public Product find(Product product) {
        var cb = em.getCriteriaBuilder();
        var c = cb.createQuery(Product.class);
        var prod = c.from(Product.class);

        var predicates = new ArrayList<Predicate>();
        predicates.add(cb.equal(prod.<User>get(USER).<Long>get(ID), product.getUser().getId()));

        if (product.getStatus() != null) {
            predicates.add(cb.equal(prod.<String>get(STATUS), product.getStatus()));
        } else {
            predicates.add(cb.equal(prod.<String>get(STATUS), Status.ACTIVE.getDescription()));
        }

        if (product.getId() != null) {
            predicates.add(cb.equal(prod.<Long>get(ID), product.getId()));
        } else {
            if (product.getName() != null) {
                predicates.add(cb.equal(prod.<String>get(NAME), product.getName()));
            }

            if (product.getDescription() != null) {
                predicates.add(cb.equal(prod.<String>get(DESCRIPTION), product.getDescription()));
            }
        }

        c.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Product> q = em.createQuery(c);
        return q.getResultStream().findFirst().orElse(null);
    }

    /**
     * Find all.
     *
     * @return the list of products
     */
    public List<Product> findAll(Product product) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Product> c = cb.createQuery(Product.class);
        Root<Product> prod = c.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(prod.<User>get(USER).<Long>get(ID), product.getUser().getId()));
        if (product.getStatus() != null) {
            predicates.add(cb.equal(prod.<String>get(STATUS), product.getStatus()));
        } else {
            predicates.add(cb.equal(prod.<String>get(STATUS), Status.ACTIVE.getDescription()));
        }

        if (product.getCategory() != null) {
            predicates.add(cb.equal(prod.<Category>get(CATEGORY).get(ID), product.getCategory().getId()));
        }

        c.where(cb.and(predicates.toArray(new Predicate[0])));
        c.orderBy(cb.asc(prod.<Long>get(ID)));

        TypedQuery<Product> q = em.createQuery(c);
        return q.getResultList();
    }

    /**
     * Find all by category category
     *
     * @param category the category category
     * @return the list
     */
    public List<Product> findAllByCategory(Category category) {
        String jpql = "SELECT p FROM Product p WHERE LOWER(p.category.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        List<Product> resultList = em.createQuery(jpql, Product.class).setParameter(NAME, category.getName()).getResultList();
        closeTransaction();
        return resultList;
    }

}
