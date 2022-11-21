package com.portappfolio.app.company.branch.zipCode;

import com.portappfolio.app.appUser.phone.Prefix;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class ZipCodeService {
    private final ZipCodeRepository zipCodeRepository;

    public Optional<ZipCode> getByZipCode(String zipCode){
        return zipCodeRepository.findByZipCode(zipCode);
    }

    public ZipCode save(ZipCode zipCode){
        if (this.getByZipCode(zipCode.getZipCode()).isPresent()){
            return null;
        }
        return zipCodeRepository.save(zipCode);
    }

    public Collection<ZipCode> list(){
        return zipCodeRepository.findAll();
    }
}
