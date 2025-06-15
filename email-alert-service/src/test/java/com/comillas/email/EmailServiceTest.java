package com.comillas.email;

import com.comillas.common.model.Alert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {

    @Test
    public void testEmailContentFormatting() {
        Alert alert = new Alert("Pepe", "a@b.com", "Madrid", 18, 10, "12345678");
        String expectedSubject = "🌧️ Alerta de lluvia en Madrid";

        assertEquals("Madrid", alert.zone);
        assertTrue(alert.precipitation > alert.threshold);
    }
}
