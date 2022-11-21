package com.portappfolio.app.identification.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long> {
    Optional<ConfirmationToken> findByToken(String token);

    @Query(value = "select c from ConfirmationToken c where c.token = ?1 and c.appUser.id = ?2")
    Optional<ConfirmationToken> findByTokenAndAppUser(String token, Long appUserId);

}
