package com.comillas.ingestor.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather{
    private int id;
    private String main;
    private String description;
    private String icon;
}

