package com.vue.app.service;

import java.util.*;
import java.util.stream.Collectors;

import com.vue.app.chat.ChatSession;
import com.vue.app.repo.dao.ContactDAO;
import com.vue.app.repo.dao.UserDAO;
import com.vue.app.repo.model.Contact;
import com.vue.app.repo.model.Message;
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

    @Autowired
    MessageService messageService;

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
    public Long count() {
        return userDAO.count();
    }

    @Transactional
    public User register(User user) {

        Random random = new Random();

        user.setEnabled(1)
            .setRole("ROLE_USER")
            .setAvatar(String.valueOf(random.nextInt(100 - 1) + 1))
            .setLastLogin(new Date());

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

    /*///////  Contactos  ///////*/
    @Transactional
    public boolean contactExists(String user, String contact) {
        List<Contact> foundContacts = contactDAO.findByUserAndContact(user, contact);
        return !foundContacts.isEmpty();
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
    public Optional<User> saveContact(String username, String contactName) {
        if (exists(contactName)) {
            Contact contact = new Contact(username, contactName);
            contactDAO.save(contact);

            User user = findById(contactName).get();
            return Optional.of(toPublicUser(user));
        } else {
            return Optional.empty();
        }

    }

    public User toPublicUser(User user) {
        return new User().setUsername(user.getUsername()).setAvatar(user.getAvatar()).setLastLogin(user.getLastLogin());
    }

    public ChatSession buildChatSession(String username, List<String> connections, Map<String, Boolean> writtingUsers) {
        User user = toPublicUser(findById(username).get());

        List<User> contacts = getContactsByUser(username).stream()
                .map(usr -> toPublicUser(usr))
                .collect(Collectors.toList());

        List<Message> messages = messageService.findByFromOrTo(username);

        return new ChatSession(user, contacts, messages, connections, writtingUsers);
    }
}