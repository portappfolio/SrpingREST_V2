package com.portappfolio.app.appUser.phone;

import com.portappfolio.app.company.branch.zipCode.Country;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class PrefixService {

    private final PrefixRepository prefixRepository;

    public Optional<Prefix> getByPrefix(String prefix){
        return prefixRepository.findByPrefix(prefix);
    }

    public Prefix save(String prefix, Country country){
        if (this.getByPrefix(prefix).isPresent()){
            return null;
        }
        return prefixRepository.save(new Prefix(prefix, country));
    }

    public Collection<Prefix> list(){
        return prefixRepository.findAll();
    }
}
