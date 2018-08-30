package com.vue.app.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vue.app.repo.dao.GroupDAO;
import com.vue.app.repo.dao.MessageDAO;
import com.vue.app.repo.model.Group;
import com.vue.app.repo.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("messageService")
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    GroupDAO groupDAO;

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

    public List<Message> markReaded(List<Message> messages, String receiver) {

        messages = messages.stream()
                .filter(msg -> msg.getReceiver().equals(receiver))
                .peek(msg -> msg.setReaded(true))
                .map(msg -> save(msg))
            .collect(Collectors.toList());
        return messages;
    }

    /*///////  Grupos  ///////*/
    @Transactional
    public Group createGroup(Group group) {
        groupDAO.save(group);
        return findGroup(group.getName()).get();
    }

    private Optional<Group> findGroup(String name) {
        List<Group> groups = groupDAO.findByName(name);

        if (!groups.isEmpty()) {
            return Optional.of(groups.get(groups.size()-1));
        } else {
            return Optional.empty();
        }
    }

    @Transactional
    public Group registerGroupUser(Group group, String username) {
        group.getUsers();
        group.getUsers().add(username);
        return groupDAO.save(group);
    }
}