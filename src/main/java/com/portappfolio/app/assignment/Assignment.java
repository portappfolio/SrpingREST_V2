package com.portappfolio.app.assignment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.assignment.Role.AssignmentRole;
import com.portappfolio.app.company.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@NoArgsConstructor
@Entity
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private UserInfo userInfo;

    @ManyToOne(fetch = FetchType.EAGER)
    private Company company;

    @JsonIgnore
    @ManyToOne
    private UserInfo requestedBy;

    @Enumerated(EnumType.STRING)
    private RequestedByType requestedByType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    UserInfo approvedBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    UserInfo endedBy;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    UserInfo rejectedBy;

    @Column(nullable = false,name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false,name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "assignment_assignment_roles", joinColumns = @JoinColumn(name = "assignment_id"),
            inverseJoinColumns = @JoinColumn(name = "assignment_role_id"))
    private Collection<AssignmentRole> roles;

    @Column(nullable = false)
    private Boolean enabled;

    @Column(nullable = false)
    private Boolean ended;

    @Column(nullable = false)
    private Boolean rejected;

    public Assignment(UserInfo userInfo, Company company, UserInfo requestedBy, RequestedByType requestedByType) {
        this.userInfo = userInfo;
        this.company = company;
        this.requestedBy = requestedBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.requestedByType = requestedByType;
        this.enabled = false;
        this.ended = false;
        this.rejected = false;
    }

    public Assignment(UserInfo userInfo, Company company, UserInfo requestedBy, UserInfo approvedBy, RequestedByType requestedByType) {
        this.userInfo = userInfo;
        this.company = company;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.requestedByType = requestedByType;
        this.enabled = true;
        this.ended = false;
        this.rejected = false;
    }
}
