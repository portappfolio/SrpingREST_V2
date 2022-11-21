package com.portappfolio.app.appUser.userInfo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class JobService {
    private final JobRepository jobRepository;

    public Optional<Job> getByName (String name){
        return jobRepository.findByName(name);
    }

    public Job save(String name){
        if (this.getByName(name).isPresent()){
            return null;
        }

        return jobRepository.save(new Job(name));
    }

    public Collection<Job> list(){
        return jobRepository.findAll();
    }
}
