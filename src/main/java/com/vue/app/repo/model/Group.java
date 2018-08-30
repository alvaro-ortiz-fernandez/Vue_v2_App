package com.vue.app.repo.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "groups")
@SecondaryTables(value = {
        @SecondaryTable(name = "group_members", pkJoinColumns = @PrimaryKeyJoinColumn(name = "group_id", referencedColumnName = "id"))
})
public class Group {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="group_members", joinColumns=@JoinColumn(name="group_id"))
    @Column(name="user")
    private Set<String> users;

    public Group() {}

    public Group(String name, Set<String> users) {
        this.name = name;
        this.users = users;
    }

    public Group(int id, String name, Set<String> users) {
        this.id = id;
        this.name = name;
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }
}