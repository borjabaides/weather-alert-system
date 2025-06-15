package com.comillas.alert;

import com.comillas.common.model.User;
import com.comillas.common.model.weather.WeatherEnriched;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertWeatherTest {

    @Test
    public void testThresholdTriggering() {
        User user = new User("Test User",1, "test@mail.com", "Madrid", 10);
        WeatherEnriched weather = new WeatherEnriched("id", "Madrid", 12, "12345678", "Europe/Madrid");

        assertTrue(weather.precipitation >= user.threshold, "Should trigger alert");
    }
}
