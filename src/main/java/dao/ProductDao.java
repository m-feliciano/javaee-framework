package dao;

import domain.Product;

import javax.persistence.EntityManager;
import java.util.List;

public class ProductDao {

    private final EntityManager em;

    public ProductDao(EntityManager em) {
        this.em = em;
    }

    public Product save(Product product) {
        this.em.getTransaction().begin();
        this.em.persist(product);
        this.em.getTransaction().commit();
        return this.em.merge(product);
    }

    public void update(Product product) {
        this.em.getTransaction().begin();
        this.em.merge(product);
        this.em.getTransaction().commit();
    }

    public boolean delete(Long id) {
        Product prod = this.findById(id);
        if (prod != null) {
            this.em.getTransaction().begin();
            this.em.remove(prod);
            this.em.getTransaction().commit();
            return true;
        }
        return false;
    }

    public Product findById(Long id) {
        return this.em.find(Product.class, id);
    }

    public List<Product> findAll() {
        String jpsl = "SELECT p FROM Product p";
        return em.createQuery(jpsl, Product.class).getResultList();
    }

    public List<Product> findAllByName(String name) {
        String jpsl = "SELECT p FROM Product p WHERE p.name = :name";
        return em.createQuery(jpsl, Product.class).setParameter("name", name).getResultList();
    }

    public List<Product> findAllByCategoryName(String name) {
        String jsql = "SELECT p FROM Product p WHERE p.category.name = :name";
        return em.createQuery(jsql, Product.class).setParameter("name", name).getResultList();
    }

}
