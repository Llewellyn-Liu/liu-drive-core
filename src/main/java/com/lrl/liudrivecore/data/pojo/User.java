package com.lrl.liudrivecore.data.pojo;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "DR_USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column
    protected String username;

    @Column
    protected String password;

    @Column
    protected ZonedDateTime accountCreated;

    @Column
    protected String userId;

    @Column
    protected String tags;

    public User() {
    }

    public User(String username, String password, String userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ZonedDateTime getAccountCreated() {
        return accountCreated;
    }

    public void setAccountCreated(ZonedDateTime accountCreated) {
        this.accountCreated = accountCreated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", accountCreated=" + accountCreated +
                ", userId='" + userId + '\'' +
                '}';
    }

}
