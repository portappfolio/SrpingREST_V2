package com.portappfolio.app.appUser.role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Roles name;

    @Column(name = "view_name")
    private String viewName;

    @Column(name = "material_icon")
    private String materialIcon;

    @Column(name = "path")
    private String path;

    @Column(name = "filter_option")
    private String filterOption;

    @Column(name = "filter_option_path")
    private String filterOptionPath;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "father_icon")
    private String fatherIcon;


    public Role(Roles role){
        this.name = role;
    }


}
