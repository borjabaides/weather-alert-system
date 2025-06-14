package com.comillas.email;

import com.comillas.user.UserPublisher;
import com.comillas.alert.model.Alert;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import java.util.*;

public class AlertEmailService {

    public static void main(String[] args) throws Exception {
        Properties smtpProps = UserPublisher.loadPropertiesFromClasspath("email-alert-service","smtp.properties");
        String smtpUser = smtpProps.getProperty("smtp.user");
        String smtpPass = smtpProps.getProperty("smtp.pass");

        // Kafka consumer config
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "localhost:9092");
        kafkaProps.put("group.id", "email-alert-service");
        kafkaProps.put("key.deserializer", StringDeserializer.class.getName());
        kafkaProps.put("value.deserializer", StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(kafkaProps);
        consumer.subscribe(Collections.singletonList("alerts"));

        ObjectMapper mapper = new ObjectMapper();

        System.out.println("📨 EmailAlertService activo... esperando alertas");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(java.time.Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                Alert alert = mapper.readValue(record.value(), Alert.class);
                sendMail(alert, smtpUser, smtpPass);
            }
        }
    }

    private static void sendMail(Alert alert, String user, String pass) {
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.host", "smtp.gmail.com");
        mailProps.put("mail.smtp.port", "587");

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(alert.mail));
            msg.setSubject("🌧️ Alerta de lluvia en tu zona, " + alert.zone);
            msg.setText(String.format(
                    "%s,\n\nSe ha detectado una precipitación de %d mm en tu zona (%s), lo cual supera tu umbral de %d mm.\n\nMantente alerta.\n\n— Weather Alert System",
                    alert.name, alert.precipitation, alert.zone, alert.threshold
            ));
            Transport.send(msg);
            System.out.printf("✅ Email enviado a %s%n", alert.mail);
        } catch (MessagingException e) {
            System.err.printf("❌ Error al enviar correo a %s: %s%n", alert.mail, e.getMessage());
        }
    }
}
