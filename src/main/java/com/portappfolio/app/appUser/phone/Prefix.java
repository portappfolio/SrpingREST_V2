package com.portappfolio.app.appUser.phone;

import com.portappfolio.app.company.branch.zipCode.Country;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class Prefix {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prefix;

    @ManyToOne
    private Country country;

    public Prefix(String prefix, Country country) {
        this.prefix = prefix;
        this.country = country;
    }
}
