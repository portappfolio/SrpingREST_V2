package com.portappfolio.app.identification;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserRepository;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.email.EmailValidator;
import com.portappfolio.app.identification.token.ConfirmationToken;
import com.portappfolio.app.identification.token.ConfirmationTokenService;
import com.portappfolio.app.models.ChangePasswordRequest;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.models.RegistrationRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final AppUserService appUserService;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AppUserRepository appUserRepository;

    public ResponseEntity<?> register(RegistrationRequest request) {
        boolean isValidEmail = emailValidator.test(request.getEmail());
        if (!isValidEmail){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("El email no tiene el formato correcto.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        ResponseEntity<?> reponse =  appUserService.signUpUser(
                new AppUser(
                        request.getFirstName(),
                        request.getLastName(),
                        request.getEmail(),
                        request.getPassword()
                )
        );
        return reponse;
    }

    @Transactional
    public ResponseEntity<?> confirmToken(String token, Long appUserId){
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService
                .getTokenByTokenAndAppUser(token,appUserId);

        if (confirmationToken.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Codigo de seguridad no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }
        if (confirmationToken.get().getConfirmedAt() != null){
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

        LocalDateTime expiredAt = confirmationToken.get().getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Token expirado.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        confirmationTokenService.setConfirmedAt(token,appUserId);
        appUserService.enableAppUser(
                confirmationToken.get().getAppUser().getEmail()
        );

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Email confirmado con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

    @Transactional
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest){

        String token = changePasswordRequest.getToken();
        Long appUserId = changePasswordRequest.getAppUserId();
        String newPassword = changePasswordRequest.getNewPassword();

        if(token.isEmpty() || newPassword.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("No se recibieron los parámetros necesarios.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Optional<ConfirmationToken> confirmationToken = confirmationTokenService
                .getTokenByTokenAndAppUser(token,appUserId);

        if (confirmationToken.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Codigo de seguridad no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (confirmationToken.get().getConfirmedAt() != null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Contraseña ya fue actualizada, solicitar un nuevo cambio de contraseña.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        LocalDateTime expiredAt = confirmationToken.get().getExpiredAt();
        if (expiredAt.isBefore(LocalDateTime.now())){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Codigo de seguridad expiró, solicitar uno nuevo.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        confirmationTokenService.setConfirmedAt(token,appUserId);

        AppUser appUser = confirmationToken.get().getAppUser();
        String encodedPassword = bCryptPasswordEncoder.encode(newPassword);
        appUser.setPassword(encodedPassword);
        appUserRepository.save(appUser);
        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Contraseña actualizada con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }
}
