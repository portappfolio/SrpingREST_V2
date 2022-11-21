package com.portappfolio.app.appUser.phone;

import com.portappfolio.app.identification.token.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PhoneRepository extends JpaRepository<Phone,Long> {

    @Query(value = "select p from Phone p where p.number = ?1 and p.prefix.prefix = ?2")
    Optional<Phone> findByNumberAndPrefix(String number, String prefix);


}
