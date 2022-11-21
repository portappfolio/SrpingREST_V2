package com.portappfolio.app.appUser.role;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class RoleService{

    private final RoleRepository roleRepository;

    public Optional<Role> getByName (Roles roles){
        return roleRepository.findByName(roles);
    }

    public Role save(Roles role){
        if (this.getByName(role).isPresent()){
            return null;
        }

        return roleRepository.save(new Role(role));
    }

}
