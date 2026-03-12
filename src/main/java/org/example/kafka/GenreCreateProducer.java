package org.example.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GenreCreateProducer {

    private final KafkaTemplate<String, GenreCreateCommand> kafkaTemplate;
    private final String topic;

    public GenreCreateProducer(
            KafkaTemplate<String, GenreCreateCommand> kafkaTemplate,
            @Value("${app.kafka.genre-create-topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(GenreCreateCommand command) {
        kafkaTemplate.send(topic, command);
    }
}
