package dao;

import domain.Inventory;

import javax.persistence.EntityManager;
import java.util.List;

public class InventoryDao extends BaseDao {

    public InventoryDao(EntityManager em) {
        super(em);
    }

    /**
     * Save.
     * receives an inventory and saves it in the database.
     *
     * @param item the inventory
     */

    public void save(Inventory item) {
        beginTransaction();
        this.em.persist(item);
        commitTransaction();
        closeTransaction();
    }

    /**
     * update item.
     * receives an inventory and updates it in the database.
     *
     * @param item the inventory to update
     */

    public void update(Inventory item) {
        beginTransaction();
        this.em.merge(item);
        commitTransaction();
        closeTransaction();
    }

    /**
     * delete item.
     * receives an id and deletes it from the database.
     *
     * @param id the id
     */

    public boolean delete(Long id) {
        Inventory item = this.findById(id);
        if (item != null) {
            beginTransaction();
            this.em.remove(item);
            commitTransaction();
            closeTransaction();
            return true;
        }
        closeTransaction();
        return false;
    }

    /**
     * Find by id.
     * receives an id and returns the inventory with that id.
     *
     * @param id the id
     * @return the inventory
     */

    public Inventory findById(Long id) {
        Inventory inventory = this.em.find(Inventory.class, id);
        closeTransaction();
        return inventory;
    }

    /**
     * Find.
     * returns all the inventories in the database.
     *
     * @return the inventories
     */

    public List<Inventory> findAll() {
        String jpql = "SELECT p FROM Inventory p ORDER BY p.id";
        List<Inventory> inventories = em.createQuery(jpql, Inventory.class).getResultList();
        closeTransaction();
        return inventories;
    }

    /**
     * Find by name.
     * receives a name and returns all the inventories with that description.
     *
     * @param name the name
     * @return the inventories
     */

    public List<Inventory> findAllByProductName(String name) {
        String jpql = "SELECT i FROM Inventory i WHERE LOWER(i.product.name) LIKE LOWER(CONCAT('%', :name, '%'))";
        List<Inventory> inventories = em.createQuery(jpql, Inventory.class).setParameter("name", name).getResultList();
        closeTransaction();
        return inventories;
    }

    /**
     * Find by description.
     * receives a name and returns all the inventories with that name.
     *
     * @param description the name
     * @return the inventories
     */

    public List<Inventory> findAllByDescription(String description) {
        String jpql = "SELECT i FROM Inventory i WHERE LOWER(i.description) LIKE LOWER(CONCAT('%', :description, '%'))";
        List<Inventory> inventories = em.createQuery(jpql, Inventory.class).setParameter("description", description + '%').getResultList();
        closeTransaction();
        return inventories;
    }

}
