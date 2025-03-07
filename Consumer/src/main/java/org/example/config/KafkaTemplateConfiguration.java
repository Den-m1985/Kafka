package org.example.config;

import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.model.MyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaTemplateConfiguration {
    private final String TEST_GROUP = "test-group";
    private final String MY_TOPIC = "test-topic";

    @Bean
    @SneakyThrows
    public ConsumerFactory<String, MyEvent> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, InetAddress.getLocalHost().getHostName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, TEST_GROUP);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // чтоб не писать так: JsonDeserializer.TRUSTED_PACKAGES, "org.example.model"
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, MyEvent.class.getCanonicalName());
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    ConcurrentMessageListenerContainer<String, MyEvent> kafkaListenerContainer(
            ConsumerFactory<String, MyEvent> factory,
            MessageListener<String, MyEvent> listener,
            KafkaTemplate<byte[], byte[]> template) {
        ContainerProperties containerProperties = new ContainerProperties(MY_TOPIC);
        containerProperties.setMessageListener(listener);

        // дублирует сообщения с ошибками в новый топик, он забирает логи из Spring
        DeadLetterPublishingRecoverer recover = new DeadLetterPublishingRecoverer(template);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recover);

        ConcurrentMessageListenerContainer<String, MyEvent> container = new ConcurrentMessageListenerContainer<>(factory, containerProperties);
        container.setConcurrency(4);  // устанавливаем кол-во потоков
        container.setCommonErrorHandler(errorHandler);
        return container;
    }

}
