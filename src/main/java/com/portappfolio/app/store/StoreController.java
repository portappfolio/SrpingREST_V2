package com.portappfolio.app.store;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.userInfo.UserInfo;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.security.config.SessionService;
import com.portappfolio.app.security.config.UserClassSecurity;
import com.portappfolio.app.store.mercadopago.models.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@PreAuthorize("isAuthenticated()")
@RestController
@AllArgsConstructor
@RequestMapping("/store")
public class StoreController {
    private final MercadoPagoService mercadoPagoService;
    private final SessionService sessionService;
    private final AppUserService appUserService;

    @PreAuthorize("hasAuthority('ADMIN_PAYMENTS')")
    @PostMapping("/createPreference")
    public ResponseEntity<?> createPreference(
            @RequestBody Preference preference
    ) throws JsonProcessingException {

        UserClassSecurity userClassSecurity = sessionService.getSession();
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());
        //Optional<UserInfo> userInfoOptional = userInfoService.getByEmail(userClassSecurity.getUsername());
        UserInfo userInfo = appUser.get().getUserInfo();

        return mercadoPagoService.createPreference(
                //TODO: Cuando se cree orden setear aditional info y expternal reference
                "Aditional Info"
                , "IDPruebas1A"
                , preference.getItems()
                , appUser.get().getEmail()
                , appUser.get().getFisrtName()
                , appUser.get().getLastName()
        );

    }
}
