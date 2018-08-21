package com.vue.app.repo.model;

import javax.persistence.*;
import java.util.Date;

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
    @Column(name = "avatar")
    private String avatar;
    @Column(name = "lastlogin")
    private Date lastLogin;
    @Column(name = "role", table = "user_roles")
    private String role;

    public User() {}

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String password, int enabled, String avatar, Date lastLogin, String role) {
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.avatar = avatar;
        this.lastLogin = lastLogin;
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

    public String getAvatar() {
        return avatar;
    }

    public User setAvatar(String avatar) {
        this.avatar = avatar;
        return this;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public User setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
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
        return "User { " + "username: " + username + ", password: " + password + ", enabled: " + enabled
                + ", avatar: " + avatar + ", lastLogin: " + lastLogin + ", role: " + role + " }";
    }
}