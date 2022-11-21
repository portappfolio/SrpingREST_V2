package com.portappfolio.app.assignment.Role;

import com.portappfolio.app.appUser.role.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AssignmentRoleRepository extends JpaRepository<AssignmentRole,Long> {
    Optional<AssignmentRole> findByName(Roles name);
}
