package com.portappfolio.app.appUser.userInfo;

import com.portappfolio.app.appUser.role.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Speciality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    private Job job;

    public Speciality(String name, Job job) {
        this.name = name;
        this.job = job;
    }
}
