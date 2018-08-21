package com.vue.app.service;

import java.util.List;

import com.vue.app.repo.dao.MessageDAO;
import com.vue.app.repo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("messageService")
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    @Transactional
    public List<Message> getAll() {
        return (List<Message>) messageDAO.findAll();
    }

    @Transactional
    public List<Message> findBySender(String sender) {
        return messageDAO.findBySender(sender);
    }

    @Transactional
    public List<Message> findByFromOrTo(String user) {
        return messageDAO.findBySenderOrReceiver(user, user);
    }

    @Transactional
    public Message save(Message message) {
        return messageDAO.save(message);
    }

    @Transactional
    public Long count() {
        return messageDAO.count();
    }
}