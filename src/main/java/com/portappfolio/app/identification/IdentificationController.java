package com.portappfolio.app.identification;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.role.RoleRepository;
import com.portappfolio.app.appUser.role.Roles;
import com.portappfolio.app.models.*;
import com.portappfolio.app.security.config.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "identification")
@AllArgsConstructor
public class IdentificationController {

    private final RegistrationService registrationService;
    private final AppUserService appUserService;
    private final JwtProvider jwtProvider;
    private final RoleRepository roleRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    //OK
    @PostMapping("registration")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request){
        return registrationService.register(request);
    }

    //OK
    @PostMapping("login")
    public ResponseEntity<JwtRequest> login(@RequestBody LoginRequest loginRequest){
        Optional<AppUser> appUser = appUserService.getByEmail(loginRequest.getEmail());

        if (appUser.isEmpty()){
            throw new Error("Usuario no existe.");
        }

        if(!appUser.get().getEnabled()){
            throw new Error("No se ha confirmado la cuenta en el email.");
        }

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        JwtRequest jwtRequest = jwtProvider.generateToken(authentication);
        return new ResponseEntity<>(jwtRequest, HttpStatus.OK);
    }

    //OK
    @GetMapping("confirm")
    public ResponseEntity<?> confirmToken(@RequestParam(name = "token", required = true) String token, @RequestParam(name = "appUser",required = true) Long appUser){
        return registrationService.confirmToken(token,appUser);
    }

    //OK
    @GetMapping("resend")
    public ResponseEntity<?> reSendEmailVerification(@RequestParam(name = "email",required = true) String email){
        return appUserService.resendEmailConfirmation(email);
    }

    //OK
    @GetMapping("sendTokenToChangePassword")
    public ResponseEntity<?> sendTokenToChangePassword(@RequestParam(name = "email",required = true) String email){
        return appUserService.sendTokenToChangePassword(email);
    }
    //OK
    @PostMapping("changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest){
        return registrationService.changePassword(changePasswordRequest);
    }

}
