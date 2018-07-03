package com.vue.app.service;

import java.util.List;
import java.util.Optional;

import com.vue.app.repo.dao.UserDAO;
import com.vue.app.repo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Transactional
    public List<User> getAll() {
        return (List<User>) userDAO.findAll();
    }

    @Transactional
    public List<User> findByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    @Transactional
    public Optional<User> findById(String username) {
        return Optional.of(userDAO.findOne(username));
    }

    @Transactional
    public boolean exists(String username) {
        return userDAO.exists(username);
    }

    @Transactional
    public void delete(String username) {
        userDAO.delete(username);
    }

    @Transactional
    public User save(User user) {
        return userDAO.save(user);
    }

    @Transactional
    public User update(User user) {
        return userDAO.save(user);
    }

    @Transactional
    public Long count() {
        return userDAO.count();
    }

    @Transactional
    public User register(User user) {
        user.setEnabled(1)
            .setRole("ROLE_USER");

        return save(user);
    }
}