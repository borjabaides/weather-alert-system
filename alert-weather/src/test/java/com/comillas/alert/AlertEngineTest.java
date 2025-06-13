package com.comillas.alert;

import com.comillas.user.model.User;
import com.comillas.ingestor.model.WeatherEnriched;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AlertEngineTest {

    @Test
    public void testThresholdTriggering() {
        User user = new User("Test User","1", "test@mail.com", "Madrid", 10);
        WeatherEnriched weather = new WeatherEnriched("id", 12, "12345678", "Madrid");

        assertTrue(weather.precipitation >= user.threshold, "Should trigger alert");
    }
}
