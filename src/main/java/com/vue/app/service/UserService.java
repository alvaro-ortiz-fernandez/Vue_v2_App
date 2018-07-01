package com.vue.app.service;

import java.util.List;

import com.vue.app.repo.dao.UserDAO;
import com.vue.app.repo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDAO<User> userDAO;

    @Transactional
    public List<User> getAll() {
        return (List<User>) userDAO.findAll();
    }

    @Transactional
    public List<User> findByUser(String user) {
        return userDAO.findByUser(user);
    }

    @Transactional
    public User findById(Long id) {
        return userDAO.findOne(id);
    }

    @Transactional
    public void delete(Long id) {
        userDAO.delete(id);
    }

    @Transactional
    public boolean add(User user) {
        return userDAO.save(user) != null;
    }

    @Transactional
    public boolean updatePerson(User person) {
        return userDAO.save(person) != null;
    }
}