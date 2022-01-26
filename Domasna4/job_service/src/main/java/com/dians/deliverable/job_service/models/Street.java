package com.dians.deliverable.job_service.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "street")
public class Street {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    @OneToMany
    private List<Address> addresses;

    public Street(String name) {
        this.name = name;
        addresses = new ArrayList<>();
    }

    public Street(String name, List<Address> addresses) {
        this.name = name;
        this.addresses = addresses;
    }
}
