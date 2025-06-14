package com.comillas.ingestor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.comillas.ingestor.model.WeatherEnriched;
import com.comillas.ingestor.model.Minutely;
import com.comillas.ingestor.model.WeatherRaw;
import com.comillas.user.UserPublisher;
import com.comillas.ingestor.model.City;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WeatherApp {

    public static void main(String[] args) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
            try {
                ScheduledTask.runWeatherTask();
            } catch (Exception e) {
                System.err.println("❌ Error ejecutando la tarea programada:");
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.MINUTES); // ⏱️ Cada 1 minuto

        System.out.println("⏳ Servicio de clima iniciado...");
    }

    static class ScheduledTask {
        static void runWeatherTask() throws Exception {

            City madrid = new City(40.4168, -3.7038);

            Properties apiProps = UserPublisher.loadPropertiesFromClasspath("weather-ingestor","api.properties");
            String apiKey = apiProps.getProperty("OWM_API_KEY");

            String url = buildOpenWeatherApiUrl(madrid, "hourly,daily", apiKey);

            // 1) HTTP GET
            URL apiUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.err.println("HTTP error code: " + conn.getResponseCode());
                conn.disconnect();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) sb.append(line);
            reader.close();
            conn.disconnect();

            String message = sb.toString();
            System.out.println("GET OpenWeather request successful with: (" + message.length() + " chars)");

            ObjectMapper mapper = new ObjectMapper();
            WeatherRaw raw = mapper.readValue(message, WeatherRaw.class);

            // kafka producer config
            Properties props = new Properties();
            props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            KafkaProducer<String, String> producer = new KafkaProducer<>(props);

            // publish raw-weather
            producer.send(new ProducerRecord<>("raw-weather", message));
            System.out.println("Request to raw-weather sent");

            // build and publish enriched-weather
            if (raw.getMinutely() != null && !raw.getMinutely().isEmpty()) {
                Minutely lastminute = raw.getMinutely().get(raw.getMinutely().size() - 1);
                WeatherEnriched enriched = new WeatherEnriched(
                        UUID.randomUUID().toString(),
                        lastminute.precipitation,
                        lastminute.dt,
                        raw.getTimezone()
                );
                String enrichedJson = mapper.writeValueAsString(enriched);
                producer.send(new ProducerRecord<>("enriched-weather", enrichedJson));
                System.out.println("Summary to enriched-weather sent: " + enrichedJson);
            }

            producer.flush();
            producer.close();
        }
    }

    private static String buildOpenWeatherApiUrl(City city, String exclude, String apiKey) {

        String BASE_URL = "https://api.openweathermap.org/data/3.0/onecall";

        return String.format("%s?lat=%f&lon=%f&exclude=%s&appid=%s",
                        BASE_URL, city.lat, city.lon, exclude, apiKey);
    }
}

