package com.portappfolio.app.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company,Long> {

    @Query(value = "select c from Company c where c.identificationType = ?1 and c.identity = ?2")
    Optional<Company> findByIdentificationTypeAndIdentity(IdentificationType identificationType, String identity);

}
