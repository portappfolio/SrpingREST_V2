package com.portappfolio.app.company.branch;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class BranchService {

    private final BranchRepository branchRepository;

    public Branch save(Branch branch){
        return branchRepository.save(branch);
    }

    public Boolean delete(Branch branch){
        if (branchRepository.findById(branch.getId()).isPresent()){
            branchRepository.deleteById(branch.getId());
            return true;
        }
        return false;
    }
}
