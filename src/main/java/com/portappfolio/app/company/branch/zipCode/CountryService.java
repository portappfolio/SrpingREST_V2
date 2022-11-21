package com.portappfolio.app.company.branch.zipCode;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CountryService {
    private final CountryRepository countryRepository;

    public Optional<Country> getByCountry(String country){
        return countryRepository.findByCountry(country);
    }

    public Country save(Country country){
        if (this.getByCountry(country.getCountry()).isPresent()){
            return null;
        }
        return countryRepository.save(country);
    }

    public Collection<Country> list(){
        return countryRepository.findAll();
    }
}
