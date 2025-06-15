package com.comillas.user;

import com.comillas.common.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserPublisherTest {

    @Test
    public void testUserPropertiesParsed() {
        User u = new User("Juan", 1, "juan@gmail.com", "Valencia", 15);
        assertEquals("Valencia", u.zone);
        assertTrue(u.threshold >= 0);
    }
}
