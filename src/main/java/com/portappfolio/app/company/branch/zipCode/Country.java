package com.portappfolio.app.company.branch.zipCode;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String country;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double length;

    @Lob
    private byte[] flag;

    public Country(String country, Double latitude, Double length) {
        this.country = country;
        this.latitude = latitude;
        this.length = length;
    }
}
