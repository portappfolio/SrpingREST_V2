package com.portappfolio.app.company.branch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portappfolio.app.company.Company;
import com.portappfolio.app.company.branch.zipCode.ZipCode;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    private ZipCode zipCode;

    @JsonIgnore
    @ManyToOne
    private Company company;

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

}
