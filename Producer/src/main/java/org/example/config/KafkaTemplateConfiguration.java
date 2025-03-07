package org.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.model.MyEvent;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaTemplateConfiguration {
    private final String MY_TOPIC = "test-topic";

        @Bean
    @SneakyThrows
    public ProducerFactory<String, MyEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.ACKS_CONFIG, "all"); // убеждаемся, что все партиции accept
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, MyEvent> kafkaTemplate(ProducerFactory<String, MyEvent> producerFactory) {
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setDefaultTopic(MY_TOPIC);
        return kafkaTemplate;
    }

    // подставляем ObjectMapper в JsonSerializer т.к. в Spring и Kafka они разные
    @Bean
    DefaultKafkaProducerFactoryCustomizer serializerCustomizer(ObjectMapper objectMapper) {
     JsonSerializer jsonSerializer = new JsonSerializer<>(objectMapper);
     return producerFactory -> producerFactory.setValueSerializer(jsonSerializer);
    }
}
