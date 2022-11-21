package com.portappfolio.app.company.branch.zipCode;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
public class ZipCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private City city;

    @Column(name = "zip_code",nullable = false)
    private String zipCode;

    @Column(nullable = false)
    private String neighborhood;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double length;

    public ZipCode( City city, String zipCode, String neighborhood, Double latitude, Double length) {
        this.city = city;
        this.zipCode = zipCode;
        this.neighborhood = neighborhood;
        this.latitude = latitude;
        this.length = length;
    }
}
