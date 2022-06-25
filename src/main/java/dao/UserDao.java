package dao;

import domain.User;

import javax.persistence.EntityManager;

public class UserDao {

    private final EntityManager em;

    public UserDao(EntityManager em) {
        this.em = em;
    }

    public void save(User user) {
        this.em.persist(user);
        this.em.flush();
    }

    public void update(User user) {
        this.em.merge(user);
    }

    public boolean delete(Long id) {
        User prod = this.findById(id);
        if (prod != null) {
            this.em.remove(prod);
            this.em.flush();
            return true;
        }
        return false;
    }

    public User findById(Long id) {
        return this.em.find(User.class, id);
    }

    public User findByLogin(String login) {
        String jpsl = "SELECT u FROM User u WHERE u.login = :login";
        return em.createQuery(jpsl, User.class)
                .setParameter("login", login)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}
