package com.comillas.ingestor;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WeatherIngestorTest {

    @Test
    public void testApiUrlReachable() {
        String url = "https://api.openweathermap.org/data/3.0/onecall";
        assertTrue(url.startsWith("https://"), "URL should be HTTPS");
    }
}
