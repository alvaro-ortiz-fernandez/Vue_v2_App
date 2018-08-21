package com.vue.app.repo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "message")
    private String message;
    @Column(name = "sender")
    private String sender;
    @Column(name = "receiver")
    private String receiver;
    @Column(name = "time")
    private Date time;
    @Column(name = "readed")
    private boolean readed;

    /* Atención a este constructor por defecto que sin él los métodos de WebSocket
    que tengan este objeto como parámetro de entrada no funcionarán */
    public Message() {}

    public Message(int id, String message, String sender, String receiver, Date time, boolean readed) {
        this.id = id;
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this. time = time;
        this. readed = readed;
    }

    public int getId() {
        return id;
    }

    public Message setId(int id) {
        this.id = id;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Message setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getSender() {
        return sender;
    }

    public Message setSender(String sender) {
        this.sender = sender;
        return this;
    }

    public String getReceiver() {
        return receiver;
    }

    public Message setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public Date getTime() {
        return time;
    }

    public Message setTime(Date time) {
        this.time = time;
        return this;
    }

    public boolean getReaded() {
        return readed;
    }

    public Message setReaded(boolean readed) {
        this.readed = readed;
        return this;
    }
}