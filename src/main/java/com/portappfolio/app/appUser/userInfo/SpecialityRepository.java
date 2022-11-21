package com.portappfolio.app.appUser.userInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SpecialityRepository extends JpaRepository<Speciality,Long> {
    Optional<Speciality> findByNameAndJob(String name, Job job);
}
