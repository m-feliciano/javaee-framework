package controllers;

import dao.UserDao;
import domain.User;

import javax.persistence.EntityManager;
import java.util.Objects;

public class UserController {

    private final UserDao userDao;

    public UserController(EntityManager em) {
        this.userDao = new UserDao(em);
    }

    /**
     * Save.
     * throws IllegalArgumentException if user is null
     *
     * @param user the user
     */

    public void save(User user) {
        if (Objects.isNull(user)) throw new IllegalArgumentException("The user must not be null.");
        this.userDao.save(user);
    }

    public void update(User prod) {
        this.userDao.update(prod);
    }

    public void delete(Long id) {
        this.userDao.delete(id);
    }

    public User findById(Long id) {
        return this.userDao.findById(id);
    }

    /**
     * Find.
     *
     * @return the user or null if not found
     */

    public User findByLogin(String login) {
        return userDao.findByLogin(login);
    }

}
