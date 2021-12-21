package com.dians.deliverable.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
public class Config {

    @Id
    private Long id;

    private LocalTime startTime;
    private LocalTime endTime;
    private double startLat;
    private double startLon;

    public Config() {
    }
}
