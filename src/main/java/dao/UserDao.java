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
        beginTransaction();
        this.em.persist(user);
        commitTransaction();
        closeTransaction();
    }

    /**
     * Update.
     *
     * @param user the user
     */

    public void update(User user) {
        beginTransaction();
        this.em.merge(user);
        commitTransaction();
        closeTransaction();
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
            beginTransaction();
            this.em.remove(prod);
            commitTransaction();
            closeTransaction();
            return true;
        }
        closeTransaction();
        return false;
    }

    /**
     * Find by id.
     *
     * @param id the id
     * @return the user found or null if not found
     */

    public User findById(Long id) {
        User user = this.em.find(User.class, id);
        closeTransaction();
        return user;
    }

    /**
     * Find by username.
     *
     * @param login the username
     * @return the user found or null if not found
     */
    public User findByLogin(String login) {
        String jpql = "SELECT NEW User(u.id, u.login) FROM User u WHERE lower(u.login) = :login";
        User user = em.createQuery(jpql, User.class)
                .setParameter("login", login.toLowerCase())
                .getResultStream()
                .findFirst()
                .orElse(null);
        closeTransaction();
        return user;
    }

    /**
     * Find by username.
     *
     * @param user the user
     * @return the user found or null if not found
     */
    public User find(User user) {
        String jpql = "SELECT u FROM User u WHERE lower(u.login) = :login";
        User login = em.createQuery(jpql, User.class)
                .setParameter("login", user.getLogin().toLowerCase())
                .getResultStream()
                .findFirst()
                .orElse(null);
        closeTransaction();
        return login;
    }
}
