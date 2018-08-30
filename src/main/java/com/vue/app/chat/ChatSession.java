package com.vue.app.chat;

import com.vue.app.repo.model.Message;
import com.vue.app.repo.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* Clase DTO para enviar datos al usuario al conectarse
* */
public class ChatSession {

    private User user;
    private List<User> contacts;
    private List<Message> messages;
    private List<String> connections;
    private Map<String, Boolean> writtingUsers;

    public ChatSession() {
        this.contacts = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.connections = new ArrayList<>();
        this.writtingUsers = new HashMap<>();
    }

    public ChatSession(User user, List<User> contacts, List<Message> messages, List<String> connections,
            Map<String, Boolean> writtingUsers) {

        this.user = user;
        this.contacts = contacts;
        this.messages = messages;
        this.connections = connections;
        this.writtingUsers = writtingUsers;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getContacts() {
        return contacts;
    }

    public void setContacts(List<User> contacts) {
        this.contacts = contacts;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public Map<String, Boolean> getWrittingUsers() {
        return writtingUsers;
    }

    public void setWrittingUsers(Map<String, Boolean> writtingUsers) {
        this.writtingUsers = writtingUsers;
    }
}
