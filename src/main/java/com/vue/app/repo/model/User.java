package com.vue.app.repo.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
@SecondaryTables(value = {
    @SecondaryTable(name = "user_roles", pkJoinColumns = @PrimaryKeyJoinColumn(name = "username", referencedColumnName = "username"))
})
public class User {

    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "enabled")
    private int enabled;
    @Column(name = "role", table = "user_roles")
    private String role;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public User(String username, String password, int enabled, String role) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getEnabled() {
        return enabled;
    }

    public User setEnabled(int enabled) {
        this.enabled = enabled;
        return this;
    }

    public String getRole() {
        return role;
    }

    public User setRole(String role) {
        this.role = role;
        return this;
    }

    @Override
    public String toString() {
        return "User { " + "username: " + username + ", password: " + password
                + ", enabled: " + enabled + ", role: " + role + " }";
    }
}