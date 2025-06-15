package com.comillas.common.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherRaw {
    private double lat;
    private int lon;
    private String timezone;
    private int timezone_offset;
    private Current current;
    private ArrayList<Minutely> minutely;
}
