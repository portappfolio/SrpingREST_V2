package com.portappfolio.app.assignment.Role;

import com.portappfolio.app.appUser.role.Roles;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class AssignmentRoleService {

    private final AssignmentRoleRepository assignmentRoleRepository;

    public Optional<AssignmentRole> getByName (Roles roles){
        return assignmentRoleRepository.findByName(roles);
    }

    public AssignmentRole save(Roles role){
        if (this.getByName(role).isPresent()){
            return null;
        }

        return assignmentRoleRepository.save(new AssignmentRole(role));
    }
}
