package dao;

import domain.Product;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductDao {

    private final EntityManager em;

    public ProductDao(EntityManager em) {
        this.em = em;
    }

    /**
     * Save.
     *
     * @param product the product
     * @return the product saved
     */

    public Product save(Product product) {
        this.em.getTransaction().begin();
        this.em.persist(product);
        this.em.getTransaction().commit();
        product = em.merge(product);
        em.close();
        return product;
    }

    /**
     * Update.
     *
     * @param product the prod
     */

    public void update(Product product) {
        this.em.getTransaction().begin();
        this.em.merge(product);
        this.em.getTransaction().commit();
        em.close();
    }

    /**
     * delete by id.
     *
     * @param id the id
     * @return true if deleted, false if not found
     */

    public boolean delete(Long id) {
        Product prod = this.findById(id);
        if (prod != null) {
            this.em.getTransaction().begin();
            this.em.remove(prod);
            this.em.getTransaction().commit();
            em.close();
            return true;
        }
        return false;
    }

    /**
     * Find by id.
     *
     * @return product with the given id
     */

    public Product findById(Long id) {
        return this.em.find(Product.class, id);
    }

    /**
     * Find all.
     *
     * @return the list of products
     */
    public List<Product> findAll() {
        String jpql = "SELECT p FROM Product p";
        return em.createQuery(jpql, Product.class).getResultList();
    }

    /**
     * Find all by name.
     * receives a name and returns the product with that name.
     *
     * @param name the description
     * @return list of products
     */

    public List<Product> findAllByName(String name) {
        String jpql = "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        return em.createQuery(jpql, Product.class).setParameter("name", name).getResultList();
    }

    /**
     * Find all by category name
     *
     * @param name the category name
     * @return the list
     */

    public List<Product> findAllByCategoryName(String name) {
        String jpql = "SELECT p FROM Product p WHERE LOWER(p.category.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        return em.createQuery(jpql, Product.class).setParameter("name", name).getResultList();
    }

}
