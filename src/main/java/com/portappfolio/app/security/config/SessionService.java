package com.portappfolio.app.security.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionService {

    public UserClassSecurity getSession(){
        Authentication curretnAuthentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserClassSecurity) curretnAuthentication.getPrincipal();
    }

}
