package com.comillas.common.model.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)

public class Minutely {
    public String dt;
    public int precipitation;
}
