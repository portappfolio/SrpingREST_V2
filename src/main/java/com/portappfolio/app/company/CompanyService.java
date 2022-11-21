package com.portappfolio.app.company;

import com.portappfolio.app.company.branch.Branch;
import com.portappfolio.app.company.branch.BranchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final BranchService branchService;


    public Optional<Company> getByIdentificationTypeAndIdentity(IdentificationType identificationType, String identity){
        return companyRepository.findByIdentificationTypeAndIdentity(identificationType,identity);
    }

    public Optional<Company> getById(Long companyId){
        return Optional.of(companyRepository.getById(companyId));
    }

    public Company create(Company company){

        Optional<Company> companyOptional = this.getByIdentificationTypeAndIdentity(company.getIdentificationType(),company.getIdentity());

        if (companyOptional.isPresent()){
            return null;
        }
        if (company.getBranches().isEmpty()){
            return companyRepository.save(company);
        }

        Collection<Branch> branches = company.getBranches();
        company.setBranches(null);
        Company company1 = companyRepository.save(company);

        for (Branch b: branches){
            b.setCompany(company1);
            branchService.save(b);
        }

        return company1;
    }

    public Company update(Company company){
        if (company.getId() == null){
            return null;
        }
        return companyRepository.save(company);
    }

    public Boolean delete(Company company){
        if (companyRepository.findById(company.getId()).isPresent()){
            companyRepository.deleteById(company.getId());
            return true;
        }
        return false;
    }

}
