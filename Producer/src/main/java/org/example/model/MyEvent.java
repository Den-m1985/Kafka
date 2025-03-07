package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MyEvent {
    private String key;
    private LocalDateTime dateTime = LocalDateTime.now();

    @Override
    public String toString() {
        return "MyEvent {" +
                "key= " + key + '\'' +
                ", dateTime= " + dateTime +
                '}';
    }
}
