package com.vue.app.repo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "user")
    private String user;
    @Column(name = "contact")
    private String contact;

    public Contact() {}

    public Contact(String user, String contact) {
        this.user = user;
        this.contact = contact;
    }

    public Contact(int id, String user, String contact) {
        this.id = id;
        this.user = user;
        this.contact = contact;
    }

    public int getId() {
        return id;
    }

    public Contact setId(int id) {
        this.id = id;
        return this;
    }

    public String getUser() {
        return user;
    }

    public Contact setUser(String user) {
        this.user = user;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public Contact setContact(String contact) {
        this.contact = contact;
        return this;
    }
}