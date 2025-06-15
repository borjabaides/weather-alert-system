package com.comillas.common.model;

public class User {
    public String name;
    public int id;
    public String mail;
    public String zone;
    public int threshold;

    public User() {
        // Jackson need this
    }

    public User(String name, int id, String mail, String zone, int threshold) {
        this.name = name;
        this.id = id;
        this.mail = mail;
        this.zone = zone;
        this.threshold = threshold;
    }
}
