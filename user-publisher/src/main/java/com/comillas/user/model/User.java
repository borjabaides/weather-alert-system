package com.comillas.user.model;

public class User {
    public String name;
    public String user_id;
    public String mail;
    public String zone;
    public int threshold;

    public User() {
        // Jackson need this
    }

    public User(String name, String user_id, String mail, String zone, int threshold) {
        this.name = name;
        this.user_id = user_id;
        this.mail = mail;
        this.zone = zone;
        this.threshold = threshold;
    }
}
