package com.portappfolio.app.test;

import com.portappfolio.app.appUser.AppUserRepository;
import com.portappfolio.app.models.CustomResponse;
import com.portappfolio.app.security.config.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping(path = "test")
@AllArgsConstructor
public class TestController {

    private final AppUserRepository appUserRepository;
    private final SessionService sessionService;

    @GetMapping("/get")
    public ResponseEntity<?> listAppUsers(){


        return new ResponseEntity<>(
                CustomResponse.builder()
                        .timeStamp(LocalDateTime.now())
                        .message("Data successful.")
                        .status(HttpStatus.OK)
                        .statusCode(HttpStatus.OK.value())
                        .data(Map.of("users",appUserRepository.findAll()
                                    ,"Current Session: ", sessionService.getSession()))
                        .build()
                , HttpStatus.OK
        );
    }
}
