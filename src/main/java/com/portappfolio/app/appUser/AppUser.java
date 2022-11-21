
package com.portappfolio.app.appUser;

import com.portappfolio.app.appUser.role.Role;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.assignment.Assignment;
import com.sun.istack.NotNull;
import lombok.*;
import javax.persistence.*;
import java.util.Collection;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@Entity
public class AppUser{

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    @Column(nullable = false)
    private String fisrtName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ManyToMany
    @JoinTable(name = "appuser_roles", joinColumns = @JoinColumn(name = "appuser_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;

    @Column(nullable = false)
    private Boolean locked = false;

    @Column(nullable = false)
    private Boolean enabled = false;

    @ManyToOne
    private Assignment currentAssignment;

    @OneToOne
    private UserInfo userInfo;


    public AppUser(String fisrtName, String lastName, String email  ,String password) {
        this.fisrtName = fisrtName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

}
