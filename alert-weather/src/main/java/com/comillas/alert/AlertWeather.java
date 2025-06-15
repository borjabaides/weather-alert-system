package com.comillas.alert;

import com.comillas.common.model.Alert;
import com.comillas.common.model.weather.WeatherEnriched;
import com.comillas.common.model.User;
import com.comillas.user.UserLoader;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class AlertWeather {

    public static void main(String[] args) throws Exception {
        // Inicializa usuarios en memoria desde users.properties
        List<User> users = UserLoader.loadUsers("user-publisher", "users.json");
        UserLoader.indexUsersByZone(users);

        // Kafka Consumer
        Properties weatherProps = new Properties();
        weatherProps.put("bootstrap.servers", "localhost:9092");
        weatherProps.put("group.id", "alert-weather");
        weatherProps.put("key.deserializer", StringDeserializer.class.getName());
        weatherProps.put("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumerWeather = new KafkaConsumer<>(weatherProps);
        consumerWeather.subscribe(Collections.singletonList("enriched-weather"));

        Properties userProps = new Properties();
        userProps.put("bootstrap.servers", "localhost:9092");
        userProps.put("group.id", "users");
        userProps.put("key.deserializer", StringDeserializer.class.getName());
        userProps.put("value.deserializer", StringDeserializer.class.getName());
        KafkaConsumer<String, String> consumerUsers = new KafkaConsumer<>(userProps);
        consumerUsers.subscribe(Collections.singletonList("users"));

        // Kafka Producer
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer", StringSerializer.class.getName());
        producerProps.put("value.serializer", StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("AlertWeather active... Waiting events from enriched-weather");
        List<User> userList = new ArrayList<>();
        while (true) {
            ConsumerRecords<String, String> userRecords = consumerUsers.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> userRecord : userRecords) {
                User user = mapper.readValue(userRecord.value(), User.class);
                userList.add(user); // acumula usuarios
                System.out.printf("🧍 Usuario registrado: %s (%s)%n", user.name, user.zone);
            }

            ConsumerRecords<String, String> weatherRecords = consumerWeather.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> weatherRecord : weatherRecords) {
                WeatherEnriched weather = mapper.readValue(weatherRecord.value(), WeatherEnriched.class);
                List<User> usersByZone = UserLoader.getUsersForZone(weather.location);

                for (User userByzone : usersByZone) {
                    if (weather.precipitation >= userByzone.threshold) {
                        Alert alert = new Alert(userByzone.name, userByzone.mail, userByzone.zone, weather.precipitation,
                                userByzone.threshold, weather.timestamp);
                        String json = mapper.writeValueAsString(alert);
                        producer.send(new ProducerRecord<>("alerts", json));
                        System.out.printf("Alert! %s (%s mm >= umbral %d mm)%n",
                                userByzone.name, weather.precipitation, userByzone.threshold);
                    }
                }
            }
        }
    }
}
