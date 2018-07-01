package com.vue.app.repo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user")
    private String user;
    @Column(name = "pass")
    private String pass;

    public User() {}

    public User(Long id, String user, String pass) {
        this.id = id;
        this.user = user;
        this.pass = pass;
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getUser() {
        return user;
    }

    public User setUser(String user) {
        this.user = user;
        return this;
    }

    public String getPass() {
        return pass;
    }

    public User setPass(String pass) {
        this.pass = pass;
        return this;
    }

    @Override
    public String toString() {
        return "User {" + "id=" + id + ", user=" + user
                + ", pass='" + pass + '\'' + '}';
    }
}