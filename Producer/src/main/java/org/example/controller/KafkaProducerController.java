package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.MyEvent;
import org.example.service.KafkaProducerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class KafkaProducerController {
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody MyEvent event) {
        kafkaProducerService.sendMessageSimple(event);
        return "Message sent to Kafka topic";
    }

}
