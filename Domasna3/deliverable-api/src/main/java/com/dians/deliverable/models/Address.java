package com.dians.deliverable.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private int number;

    private Double lat;

    private Double lon;

    public Address() {
    }

    public Address(int number, Double lat, Double lon) {
        this.number = number;
        this.lat = lat;
        this.lon = lon;
    }
}
