package com.portappfolio.app.appUser.userInfo;

import com.portappfolio.app.appUser.role.Role;
import com.portappfolio.app.appUser.role.Roles;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class GenderService {

    private final GenderRepository genderRepository;

    public Optional<Gender> getByName (String name){
        return genderRepository.findByName(name);
    }

    public Gender save(String name){
        if (this.getByName(name).isPresent()){
            return null;
        }

        return genderRepository.save(new Gender(name));
    }

    public Collection<Gender> list(){
        return genderRepository.findAll();
    }

}
