package com.portappfolio.app.security.config;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.assignment.Assignment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserClassSecurity implements UserDetails {

    private String fisrtName;
    private String lastName;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean locked = false;
    private Boolean enabled = false;
    private Assignment currentAssignment;
    private Long appUserId;
    private UserInfo userInfo;

    public UserClassSecurity(String fisrtName, String lastName, String email, String password, Collection<? extends GrantedAuthority> authorities, Boolean locked, Boolean enabled, Assignment currentAssignment, Long appUserId, UserInfo userInfo) {
        this.fisrtName = fisrtName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.locked = locked;
        this.enabled = enabled;
        this.currentAssignment = currentAssignment;
        this.appUserId = appUserId;
        this.userInfo = userInfo;
    }

    public static UserClassSecurity build(AppUser appUser){
        List<GrantedAuthority> authorities = appUser.getRoles().stream().map(rol -> new SimpleGrantedAuthority(rol.getName().name())).collect(Collectors.toList());
        return new UserClassSecurity(
                appUser.getFisrtName()
                ,appUser.getLastName()
                ,appUser.getEmail()
                ,appUser.getPassword()
                ,authorities
                ,appUser.getLocked()
                ,appUser.getEnabled()
                ,appUser.getCurrentAssignment()
                , appUser.getId()
                , appUser.getUserInfo()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public String getFisrtName() {
        return fisrtName;
    }

    public String getLastName() {
        return lastName;
    }

    public Assignment getCurrentAssignment() {
        return currentAssignment;
    }

    public UserInfo getUserInfo(){
        return userInfo;
    }

    public Long getAppUserId() {
        return appUserId;
    }
}
