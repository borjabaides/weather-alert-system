package com.comillas.user;

import com.comillas.common.model.User;
import com.comillas.common.utils.ConfigLoader;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.*;

public class UserLoader {

    private static final Map<String, List<User>> usersByZone = new HashMap<>();

    public static List<User> loadUsers(String module, String fileName) throws Exception {

        return ConfigLoader.loadJsonList( module, fileName, new TypeReference<List<User>>() {});
    }

    public static void indexUsersByZone(List<User> users) {
        usersByZone.clear();
        for (User user : users) {
            String zoneKey = user.zone.toLowerCase();
            usersByZone.computeIfAbsent(zoneKey, z -> new ArrayList<>()).add(user);
        }
    }

    public static List<User> getUsersForZone(String zone) {
        return usersByZone.getOrDefault(zone.toLowerCase(), Collections.emptyList());
    }
}
