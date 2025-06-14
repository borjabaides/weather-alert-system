package com.comillas.ingestor.model;

public class City {
    public double lat;
    public double lon;

    public City() {
        // Jackson need this
    }

    public City(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
