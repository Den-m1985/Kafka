package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.MyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    public final String TOPIC = "test-topic";
    public final String GROUP = "my-group";

//    @KafkaListener(topics = TOPIC, groupId = GROUP)
//    public void flightEventConsumer(MyEvent message) {
//        MyEvent myEvent = message;
//        log.info(record.value().toString());
//    }

    @Bean
    MessageListener<String, MyEvent> messageListener(){
        return (record)->{
            log.info(record.topic());
            log.info(String.valueOf(record.partition()));
            log.info(String.valueOf(record.offset()));
            log.info(record.value().toString());
            log.info("");
        };
    }

}
