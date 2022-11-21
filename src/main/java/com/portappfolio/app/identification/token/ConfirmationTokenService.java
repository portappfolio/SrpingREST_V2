package com.portappfolio.app.identification.token;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ConfirmationTokenService {

    private final ConfirmationTokenRepository confirmationTokenRepository;

    public ConfirmationToken saveConfirmationToken(ConfirmationToken token){
        return confirmationTokenRepository.save(token);
    }

    public Optional<ConfirmationToken> getToken(String token){
        return confirmationTokenRepository.findByToken(token);
    }

    public Optional<ConfirmationToken> getTokenByTokenAndAppUser(String token, Long appUserId){
        return confirmationTokenRepository.findByTokenAndAppUser(token,appUserId);
    }

    public void setConfirmedAt(String token, Long appUserId) {
        ConfirmationToken confirmationToken = this.getTokenByTokenAndAppUser(token,appUserId).orElseThrow(() ->
                new IllegalStateException("token not found")
        );
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
    }
}
