package com.dians.deliverable.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor

@Entity
public class Notification {

    @SequenceGenerator(
            name = "notification_sequence",
            sequenceName = "notification_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "notification_sequence"
    )
    @Id
    private Long id;
    private String message;

    public Notification() {
    }

    public Notification(String message) {
        this.message = message;
    }
}
