package dao;

import domain.User;

import javax.persistence.EntityManager;

public class UserDao extends BaseDao {
    public UserDao(EntityManager em) {
        super(em);
    }

    /**
     * Save.
     *
     * @param user the user
     */

    public void save(User user) {
        begin();
        this.em.persist(user);
        commit();
        close();
    }

    /**
     * Update.
     *
     * @param user the user
     */

    public void update(User user) {
        begin();
        this.em.merge(user);
        commit();
        close();
    }

    /**
     * Delete by id.
     *
     * @param id the id
     * @return true if deleted, false if not found
     */

    public boolean delete(Long id) {
        User prod = this.findById(id);
        if (prod != null) {
            begin();
            this.em.remove(prod);
            commit();
            close();
            return true;
        }
        return false;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the user found or null if not found
     */

    public User findById(Long id) {
        return this.em.find(User.class, id);
    }

    /**
     * Find by username.
     *
     * @param login the username
     * @return the user found or null if not found
     */

    public User findByLogin(String login) {
        String jpql = "SELECT u FROM User u WHERE u.login = :login";
        User user = em.createQuery(jpql, User.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst()
                .orElse(null);
        close();
        return user;
    }

}
