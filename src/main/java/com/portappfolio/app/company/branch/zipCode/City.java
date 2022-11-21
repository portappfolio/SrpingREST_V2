package com.portappfolio.app.company.branch.zipCode;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    @ManyToOne
    private Country country;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double length;

    public City(String city, Country country, Double latitude, Double length) {
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.length = length;
    }
}
