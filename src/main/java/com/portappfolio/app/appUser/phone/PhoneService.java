package com.portappfolio.app.appUser.phone;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserRepository;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.phone.sms.SmsService;
import com.portappfolio.app.appUser.phone.whatsapp.WhatsAppService;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.appUser.userInfo.UserInfoService;
import com.portappfolio.app.identification.token.ConfirmationToken;
import com.portappfolio.app.identification.token.ConfirmationTokenService;
import com.portappfolio.app.identification.token.Type;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.models.PhoneRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
@Transactional
public class PhoneService {

    private final PhoneRepository phoneRepository;
    private final AppUserRepository appUserRepository;

    private final ConfirmationTokenService confirmationTokenService;
    private final SmsService smsService;
    private final WhatsAppService whatsAppService;
    private final PrefixService prefixService;
    private final AppUserService appUserService;
    private final UserInfoService userInfoService;

    public Phone save(Phone phone){
        return phoneRepository.save(phone);
    }

    public Boolean delete(Phone phone){
        if(phoneRepository.findById(phone.getId()).isPresent()) {
            phoneRepository.deleteById(phone.getId());
            return true;
        }
        return false;
    }

    public ResponseEntity<?> sendTokenToValidatePhone(String email, PhoneRequest phone){

        Optional<AppUser> appUser = appUserRepository.findByEmail(email);

        if(appUser.isEmpty()) {
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Cliente no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }
/*
        Optional<Phone> phoneOptional = phoneRepository.findByNumberAndPrefix(phone.getNumber(), phone.getPrefix());

        if(phoneOptional.isPresent()){
            if (phoneOptional.get().getConfirmed()){
                return new ResponseEntity<>(
                        CustomResponse.builder()
                                .timeStamp(LocalDateTime.now())
                                .message("Telefono ya fue confirmado.")
                                .status(HttpStatus.BAD_REQUEST)
                                .statusCode(HttpStatus.BAD_REQUEST.value())
                                .build()
                        , HttpStatus.BAD_REQUEST
                );
            }

        }

 */

        Random random = new Random();
        int number = random.nextInt(999999);
        String token = String.format("%06d",number);

        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20),
                appUser.get(),
                Type.phone
        );

        confirmationTokenService.saveConfirmationToken(confirmationToken);

        switch (phone.getChannels()){
            case sms:
                smsService.sendSms(phone.getPrefix() + phone.getNumber(),"Portappfolio. El codigo para validar tu telefono es: "+ token);
                break;

            case whatsapp:
                whatsAppService.sendWhatsapp(phone.getPrefix() + phone.getNumber(),"Portappfolio. El codigo para validar tu telefono es: "+ token);
                break;
        }

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .data(Map.of("token",token
                        ))
                        .message(phone.getChannels()+" enviado con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );
    }

    @Transactional
    public ResponseEntity<?> confirmPhone( String token , Long appUserId , String prefix, String number, Channels channel){
        Optional<ConfirmationToken> confirmationToken = confirmationTokenService.getTokenByTokenAndAppUser(token,appUserId);
        Optional<Prefix> prefixOptional = prefixService.getByPrefix(prefix);

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

        if (prefixOptional.isEmpty()){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Codigo de pais no existe.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        if (confirmationToken.get().getConfirmedAt() != null) {
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Telefono ya fue confirmado.")
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
                            .message("Codigo expirado, por favor solicitar uno nuevamente.")
                            .status(HttpStatus.BAD_REQUEST)
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .build()
                    , HttpStatus.BAD_REQUEST
            );
        }

        Phone phoneSaved = this.save(
               new Phone(
                       prefixOptional.get()
                       , number
                       , channel
                       , Types.app_user
                       , true
               )
        );

        confirmationTokenService.setConfirmedAt(token,appUserId);

        AppUser appUser = appUserService.getByAppUserId(appUserId).get();

        //Si todavía no se ha asignado un telefono al usuario, es porque se está registrando por pimera vez y se sigue el flujo ya diesñado
        if(appUser.getUserInfo() == null || appUser.getUserInfo().getPhone() == null){
            return new ResponseEntity<>(
                    CustomResponse.builder()
                            .timeStamp(LocalDateTime.now())
                            .message("Telefono confirmado con exito.")
                            .status(HttpStatus.OK)
                            .statusCode(HttpStatus.OK.value())
                            .data(Map.of(
                                    "phone", phoneSaved
                            ))
                            .build()
                    , HttpStatus.OK
            );
        }

        //Si sigue es porque se está actualizando el telefono, se debe eliminar el anterior y persistir el nuevo "phoneSaved"
        this.delete(appUser.getUserInfo().getPhone());
        UserInfo userInfo = appUser.getUserInfo();
        userInfo.setPhone(phoneSaved);
        userInfoService.update(userInfo);

        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Telefono actualizado con exito.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .build()
                , HttpStatus.OK
        );

    }

}
