package com.comillas.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.comillas.common.model.User;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.*;

public class UserPublisher {

    public static void main(String[] args) throws Exception {
        List<User> users = UserLoader.loadUsers("user-publisher", "users.json");
        UserLoader.indexUsersByZone(users);

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
}
