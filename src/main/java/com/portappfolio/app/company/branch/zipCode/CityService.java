package com.portappfolio.app.company.branch.zipCode;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CityService {

    private final CityRepository cityRepository;

    public Optional<City> getByCity(String city){
        return cityRepository.findByCity(city);
    }

    public City save(City city){
        if (this.getByCity(city.getCity()).isPresent()){
            return null;
        }
        return cityRepository.save(city);
    }

    public Collection<City> list(){
        return cityRepository.findAll();
    }
}
