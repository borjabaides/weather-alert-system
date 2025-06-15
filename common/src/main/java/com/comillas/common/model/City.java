package com.comillas.common.model;

public class City {

    public String name;
    public double lat;
    public double lon;

    public City() {
        // Jackson need this
    }

    public City(String name, double lat, double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }
}
