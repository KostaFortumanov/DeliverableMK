package com.dians.deliverable.job_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String number;

    private Double lat;

    private Double lon;

    public Address(String number, Double lat, Double lon) {
        this.number = number;
        this.lat = lat;
        this.lon = lon;
    }
}
