package com.portappfolio.app.appUser.userInfo;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.phone.Phone;
import com.portappfolio.app.assignment.Assignment;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Phone phone;

    //@OneToOne
    //private AppUser appUser; //Por el servicio session se obtiene

    @ManyToOne
    private Gender gender;

    @ManyToOne
    private Speciality speciality;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private LocalDateTime birthday;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    @Column(name = "profile_picture")
    private byte[] profilePicture;

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    //@OneToMany(fetch = FetchType.EAGER)
    //private Collection<Assignment> assignments;

    public UserInfo(Phone phone, AppUser appUser, Gender gender, Speciality speciality, LocalDateTime birthday, byte[] profilePicture) {
        this.phone = phone;
        this.gender = gender;
        this.speciality = speciality;
        this.email = appUser.getEmail();
        this.firstName = appUser.getFisrtName();
        this.lastName = appUser.getLastName();
        this.birthday = birthday;
        this.profilePicture = profilePicture;
    }

    public UserInfo(Phone phone, AppUser appUser, Gender gender, Speciality speciality, LocalDateTime birthday) {
        this.phone = phone;
        this.gender = gender;
        this.speciality = speciality;
        this.email = appUser.getEmail();
        this.firstName = appUser.getFisrtName();
        this.lastName = appUser.getLastName();
        this.birthday = birthday;
    }


}
