package com.portappfolio.app.appUser;
import com.portappfolio.app.appUser.role.Role;
import com.portappfolio.app.appUser.role.RoleService;
import com.portappfolio.app.appUser.role.Roles;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.assignment.Assignment;
import com.portappfolio.app.assignment.AssignmentService;
import com.portappfolio.app.assignment.Role.AssignmentRole;
import com.portappfolio.app.email.EmailService;
import com.portappfolio.app.identification.token.ConfirmationToken;
import com.portappfolio.app.identification.token.ConfirmationTokenService;
import com.portappfolio.app.identification.token.Type;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.security.config.UserClassSecurity;
import com.portappfolio.app.security.config.jwt.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class AppUserService implements UserDetailsService {

    private final static String USER_NOT_FOUND_MSG = "user with email %s not found";
    private final AppUserRepository appUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final RoleService roleService;
    private static final String URL_API = "http://localhost:8080";
    private static final String URL_FRONT_APP = "http://localhost:4200";

    public Optional<AppUser> getByEmail(String email){
        return appUserRepository.findByEmail(email);
    }

    public Optional<AppUser> getByAppUserId(Long appUserId) {return appUserRepository.findById(appUserId);}

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = this.getByEmail(username).orElseThrow(() -> new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username))) ;
        return UserClassSecurity.build(appUser);
    }

    public ResponseEntity<?> signUpUser(AppUser appUser){
        boolean userExist = appUserRepository.findByEmail(appUser.getEmail()).isPresent();

        if (userExist){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El email ya fue registrado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setCurrentAssignment(null);
        Collection<Role> roles = new ArrayList<>();
        roles.add(roleService.getByName(Roles.USER).get());
        appUser.setRoles(roles);
        appUserRepository.save(appUser);

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(15),
                appUser,
                Type.email
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //TODO: Enable in prod
        emailService.sendEmailVerification(appUser.getEmail(),appUser.getFisrtName(), URL_FRONT_APP+"/confirmAccount?token="+token+"&appUser="+appUser.getId(),appUser.getFisrtName()+"! Verifica tú email para acceder a nuestros servicios.","d-0484f6d1f815417dad63300c36a6f3bc");

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("token",token))
                        .message("Usario creado exitosamente e email enviado con token.")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                , HttpStatus.CREATED
        );
    }

    public AppUser save(AppUser appUser){
        String encodedPassword = bCryptPasswordEncoder.encode(appUser.getPassword());
        appUser.setPassword(encodedPassword);
        appUser.setCurrentAssignment(null);
        Collection<Role> roles = new ArrayList<>();
        roles.add(roleService.getByName(Roles.USER).get());
        appUser.setRoles(roles);
        return appUserRepository.save(appUser);
    }

    public ResponseEntity<?> resendEmailConfirmation(String email){

        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if(appUser.isEmpty()) {
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Email no está registrado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (appUser.get().getEnabled()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Email ya fue confirmado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(15),
                appUser.get(),
                Type.email
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        //DO: SEND EMAIL
        emailService.sendEmailVerification(appUser.get().getEmail(),appUser.get().getFisrtName(),URL_FRONT_APP+"/confirmAccount?token="+token+"&appUser="+appUser.get().getId(),appUser.get().getFisrtName()+"! Verifica tú email para acceder a nuestros servicios.","d-0484f6d1f815417dad63300c36a6f3bc");

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("token",token))
                        .message("Email enviado con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    public void enableAppUser(String email) {
        AppUser appUser = appUserRepository.findByEmail(email).orElseThrow(() ->
                new IllegalStateException("Email no encontrado.")
        );
        appUser.setEnabled(true);
        appUserRepository.save(appUser);
    }

    public ResponseEntity<?> sendTokenToChangePassword(String email){
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if(appUser.isEmpty()) {
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Email no está registrado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        String token = UUID.randomUUID().toString();

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(1),
                appUser.get(),
                Type.password
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        emailService.sendEmailVerification(appUser.get().getEmail(),appUser.get().getFisrtName(),URL_FRONT_APP+"/changePassword?token="+token+"&appUser="+appUser.get().getId(),appUser.get().getFisrtName()+"! Cambia tu contraseña.","d-1dd4d3e1d83c4357be9cca8faec7da19");

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("token",token
                                        ,"appUserId",appUser.get().getId()))
                        .message("Email enviado con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }

    public AppUser addUserInfo(UserInfo userInfo, AppUser appUser){
        AppUser appUserPU = appUserRepository.getById(appUser.getId());
        appUserPU.setUserInfo(userInfo);

        Collection<Role> roles = appUser.getRoles();
        roles.add(roleService.getByName(Roles.MULTI_LOGIN).get());
        appUserPU.setRoles(roles);
        return appUserRepository.save(appUserPU);
    }

    public AppUser logOutMultiLogin(AppUser appUser){
        if(appUser.getCurrentAssignment() == null){
            return appUser;
        }

        appUser.getRoles().removeIf(role -> (!role.getName().equals(Roles.USER) && !role.getName().equals(Roles.MULTI_LOGIN)));
        appUser.setCurrentAssignment(null);

        return appUserRepository.save(appUser);

    }



    //TODO:Validar Posman
    /*
    public ResponseEntity<?> clearAuthoritiesUserDetails(String email){
        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if(appUser.isEmpty()) {
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Email no está registrado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if(appUser.get().getRoles().isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El usuario no tiene Roles.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        appUser.get().setRoles(null);
        appUserRepository.save(appUser.get());

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Roles seteados con Exito")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    public ResponseEntity<?> addAuthoritie(Roles rol, String email){
        Optional<Role> roleOptional = roleService.getByName(rol);

        if (roleOptional.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Rol no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Email no está registrado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Collection<Role> actualRoles = appUser.get().getRoles();
        actualRoles.add(roleOptional.get());
        appUser.get().setRoles(actualRoles);
        appUserRepository.save(appUser.get());

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Rol agregado con exito.")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                , HttpStatus.CREATED
        );
    }

    public ResponseEntity<?> addAuthorities(Collection<Roles> roles, String email){

        Collection<Role> roleCollection = new ArrayList<>();

        for (Roles rol: roles){
            roleCollection.add(roleService.getByName(rol).get());
        }

        if (roles.size() != roleCollection.size()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Rol no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if (appUser.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Email no está registrado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Collection<Role> actualRoles = appUser.get().getRoles();
        for( Role role: roleCollection){
            actualRoles.add(role);
        }
        appUser.get().setRoles(actualRoles);
        appUserRepository.save(appUser.get());

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Roles agregado con exito.")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build()
                , HttpStatus.CREATED
        );
    }



    public ResponseEntity<?> currentAssignment(Assignment assignment){
        //Se valida la asignación esté vigente
        if (!assignment.getEnabled()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("La cuenta ya no se encuentra asignada a la empresa.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        //Se setean los roles
        AppUser appUser = assignment.getAppUser();
        Collection<AssignmentRole> assignmentRoles = assignment.getRoles();
        Collection<Role> appUserRoles = null;

        for(AssignmentRole assignmentRole: assignmentRoles){
            appUserRoles.add(roleService.getByName(assignmentRole.getName()).get());
        }
        appUser.setRoles(appUserRoles);

        //Se setea la asignación actual
        appUser.setCurrentAssignment(assignment);

        appUserRepository.save(appUser);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Se actualizó la empresa.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }
    */
}

