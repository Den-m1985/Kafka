package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.example.model.MyEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, MyEvent> kafkaTemplate;
    private String MY_TOPIC = "test-topic";

    public void sendMessageSimple(MyEvent event) {
        kafkaTemplate.send(MY_TOPIC, event);
        log.info("Producer produced the message: {}", event);
    }

    private void sendMessageWithDefaultTopic(MyEvent event){
        kafkaTemplate.sendDefault(event);
    }

    private void sendMessageWithHeader(MyEvent event) {
        var message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, MY_TOPIC)
                .setHeader(KafkaHeaders.PARTITION, 0)
                .setHeader(KafkaHeaders.KEY, "my-key")
                .build();
        kafkaTemplate.send(message);
    }

    public void sendMessageWithHeaderAndException(MyEvent event) {
        Message<MyEvent> message = MessageBuilder
                .withPayload(event)
                .setHeader(KafkaHeaders.TOPIC, MY_TOPIC)
                .build();
        kafkaTemplate.send(message).whenComplete((result, throwable) -> {
            if (throwable != null) {
                throw new RuntimeException(
                        "Произошла ошибка при попытке отправки задачи на выполнение",
                        throwable
                );
            }
            ProducerRecord<String, MyEvent> messageRecord = result.getProducerRecord();
            log.info("Задача успешно отправлена на выполнение: topic = {}, partition = {}",
                    messageRecord.topic(), messageRecord.partition());
        });
    }

    @Scheduled(fixedDelay = 7000)
    public void doYourJob(){
        MyEvent event = new MyEvent();
        event.setKey("sendMessage_____________________________________key: ");
        sendMessageSimple(event);

        event.setKey("sendMessageWithDefaultTopic____key: ");
        sendMessageWithDefaultTopic(event);

        event.setKey("sendPushAsync-_____key: ");
        sendMessageWithHeader(event);

        event.setKey("sendNewParentTask____-key: ");
        sendMessageWithHeaderAndException(event);

        log.info("");
    }

}
