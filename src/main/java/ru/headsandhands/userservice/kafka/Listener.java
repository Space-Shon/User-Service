package ru.headsandhands.userservice.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {

    @KafkaListener(topics = "user-topic", groupId = "test")
    public void Listener(String message) {
        System.out.println("Message: " + message);
    }

}