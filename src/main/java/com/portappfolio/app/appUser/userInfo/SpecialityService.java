package com.portappfolio.app.appUser.userInfo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class SpecialityService {
    private final SpecialityRepository specialityRepository;

    public Optional<Speciality> getByNameAndJob (String name, Job job){
        return specialityRepository.findByNameAndJob(name,job);
    }

    public Speciality save(String name, Job job){
        if (this.getByNameAndJob(name,job).isPresent()){
            return null;
        }
        return specialityRepository.save(new Speciality(name,job));
    }

    public Collection<Speciality> list(){
        return specialityRepository.findAll();
    }

}
