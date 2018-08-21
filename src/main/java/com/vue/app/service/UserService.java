package com.vue.app.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vue.app.repo.dao.ContactDAO;
import com.vue.app.repo.dao.UserDAO;
import com.vue.app.repo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userService")
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    ContactDAO contactDAO;

    @Transactional
    public List<User> getAll() {
        return (List<User>) userDAO.findAll();
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
    public List<User> getContactsByUser(String user) {
        return contactDAO.findByUser(user).stream()
                .map(cont -> findById(cont.getContact()))
                .map(optUser -> optUser.isPresent() ? optUser.get() : null)
                .filter(usr -> usr != null)
            .collect(Collectors.toList());
    }

    @Transactional
    public Long count() {
        return userDAO.count();
    }

    @Transactional
    public User register(User user) {
        user.setEnabled(1)
            .setRole("ROLE_USER")
            .setAvatar("1");

        return save(user);
    }

    @Transactional
    public void updateLastLogin(String username) {
        Optional<User> userOpt = findById(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(new Date());
            save(user);
        }
    }

    public User toPublicUser(User user) {
        return new User().setUsername(user.getUsername()).setAvatar(user.getAvatar()).setLastLogin(user.getLastLogin());
    }
}