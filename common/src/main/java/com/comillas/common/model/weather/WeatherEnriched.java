package com.comillas.common.model.weather;

public class WeatherEnriched {
    public String id;
    public String location;
    public int precipitation;
    public String timestamp;
    public String timezone;

    public WeatherEnriched() {
        // Jackson need this
    }

    public WeatherEnriched(String id, String location, int precipitation, String timestamp, String timezone) {
        this.id = id;
        this.location = location;
        this.precipitation = precipitation;
        this.timestamp = timestamp;
        this.timezone = timezone;
    }
}
