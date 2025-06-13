package com.comillas.ingestor.model;

public class WeatherEnriched {
    public String id;
    public int precipitation;
    public String timestamp;
    public String timezone;

    public WeatherEnriched() {
        // Jackson need this
    }

    public WeatherEnriched(String id, int precipitation, String timestamp, String timezone) {
        this.id = id;
        this.precipitation = precipitation;
        this.timestamp = timestamp;
        this.timezone = timezone;
    }
}
