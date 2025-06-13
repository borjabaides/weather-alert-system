package com.comillas.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.comillas.user.model.User;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class UserPublisher {

    private static final Map<String, List<User>> usersByZone = new HashMap<>();

    public static void main(String[] args) throws Exception {
        List<User> users = loadUsersFromClasspath();
        indexUsersByZone(users);

        // Kafka producer
        Properties kafkaProps = new Properties();
        kafkaProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        kafkaProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(kafkaProps);
        ObjectMapper mapper = new ObjectMapper();

        for (User user : users) {
            String json = mapper.writeValueAsString(user);
            producer.send(new ProducerRecord<>("users", json));
            System.out.printf("User published correctly: %s (%s)%n", user.name, user.zone);
        }

        producer.flush();
        producer.close();
    }

    public static List<User> loadUsersFromClasspath() {
        String module = "user-publisher";
        String fileName = "users.properties";
        try {
            Properties props = loadPropertiesFromClasspath(module, fileName);
            List<User> users = parseUsersFromProperties(props);
            return users;
        } catch (IOException e) {
            throw new RuntimeException("Error loading users.properties from classpath", e);
        }
    }

    public static Properties loadPropertiesFromClasspath(String module, String fileName) throws IOException {
        Properties props = new Properties();
        // 1. Try to load from classpath
        InputStream inputFile = null;
        try {
            inputFile = UserPublisher.class.getClassLoader().getResourceAsStream(fileName);
            if (inputFile != null) {
                props.load(inputFile);
                return props;
            }
        } finally {
            if (inputFile != null) {
                inputFile.close();
            }
        }
        // 2. Try to load from external path
        String commonPath = "/src/main/java/resources/";
        String externalPath = System.getProperty("user.dir") + File.separator + module + commonPath + fileName;
        if (Files.exists(Paths.get(externalPath))) {
            try (InputStream externalInputFile = new FileInputStream(externalPath)) {
                props.load(externalInputFile);
                return props;
            }
        }

        // 3. throw Except
        throw new FileNotFoundException("No se encontró el archivo '" + fileName + "' ni en el classpath ni en el sistema de archivos.");
    }

    private static List<User> parseUsersFromProperties(Properties props) {
        Set<String> ids = props.stringPropertyNames().stream()
                .filter(k -> k.startsWith("user."))
                .map(k -> k.split("\\.")[1])
                .collect(Collectors.toSet());

        List<User> users = new ArrayList<>();
        for (String id : ids) {
            String prefix = "user." + id + ".";
            String name = props.getProperty(prefix + "name");
            String mail = props.getProperty(prefix + "mail");
            String zone = props.getProperty(prefix + "zone");
            int threshold = Integer.parseInt(props.getProperty(prefix + "threshold"));
            users.add(new User(name, id, mail, zone, threshold));
        }
        return users;
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
