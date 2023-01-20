package dao;

import domain.User;
import domain.enums.Status;
import org.hibernate.jpa.QueryHints;
import servlets.utils.EncryptDecrypt;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.QueryHint;

public class UserDao extends BaseDao {

    public static final String LOGIN = "login";

    public UserDao(EntityManager em) {
        super(em);
    }

    /**
     * Save.
     *
     * @param user the user
     */
    public User save(User user) {
        user.setStatus(Status.ACTIVE.getDescription());
        beginTransaction();
        user = em.merge(user);
        commitTransaction();
        closeTransaction();
        return user;
    }

    /**
     * Update.
     *
     * @param user the userToUpdate
     */
    public User update(User user) {
        user.setStatus(Status.ACTIVE.getDescription());
        beginTransaction();
        user = em.merge(user);
        commitTransaction();
        closeTransaction();
        return user;
    }

    /**
     * Delete by id.
     *
     * @param id the id
     * @return true if deleted, false if not found
     */
    public boolean delete(Long id) {
        User user = this.findById(id);
        boolean deleted = false;
        if (user != null) {
            beginTransaction();
            user = this.em.merge(user);
            user.setStatus(Status.DELETED.getDescription());
            commitTransaction();
            deleted = true;
        }
        closeTransaction();
        return deleted;
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
     * @param user the user and password
     * @return the user found or null if not found
     */
    public User findByLogin(User user) {
        String jpql = """
                 SELECT u FROM User u
                 WHERE u.login = :login
                 AND u.status = :status
                 AND u.password = :password
                """;

        return em.createQuery(jpql, User.class)
                .setParameter(LOGIN, user.getLogin().toLowerCase())
                .setParameter("password", user.getPassword())
                .setParameter("status", Status.ACTIVE.getDescription())
                .setHint(QueryHints.HINT_READONLY, true)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Find by username.
     *
     * @param user the user
     * @return the user found or null if not found
     */
    public User find(User user) {
        String jpql = "SELECT u FROM User u JOIN FETCH u.perfis p WHERE lower(u.login) = :login";
        return em.createQuery(jpql, User.class)
                .setParameter(LOGIN, user.getLogin().toLowerCase())
                .setHint(QueryHints.HINT_READONLY, true)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
