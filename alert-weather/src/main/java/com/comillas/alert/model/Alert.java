package com.comillas.alert.model;

public class Alert {
    public String name;
    public String mail;
    public String zone;
    public int precipitation;
    public int threshold;
    public String timestamp;

    public Alert() {
        // Jackson need this
    }

    public Alert(String name, String mail, String zone, int precipitation, int threshold, String timestamp) {
        this.name = name;
        this.mail = mail;
        this.zone = zone;
        this.precipitation = precipitation;
        this.threshold = threshold;
        this.timestamp = timestamp;
    }
}
