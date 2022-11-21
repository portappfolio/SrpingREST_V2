package com.portappfolio.app.company.branch.zipCode;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZipCodeRepository extends JpaRepository<ZipCode,Long> {
    Optional<ZipCode> findByZipCode(String zipCode);
}
