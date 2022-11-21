package com.portappfolio.app.company;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.phone.Channels;
import com.portappfolio.app.assignment.Assignment;
import com.portappfolio.app.company.branch.Branch;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false)
    private IdentificationType identificationType;

    @Column(nullable = false)
    private String identity;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updated_at = LocalDateTime.now();


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "company")
    private Collection<Branch> branches;

    //Referencia Circular
    //@OneToMany
    //private Collection<Assignment> assignments;


}
