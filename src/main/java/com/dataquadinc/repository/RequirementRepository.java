package com.dataquadinc.repository;

import com.dataquadinc.model.Requirement;
import com.dataquadinc.utils.RequirementSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface RequirementRepository extends JpaRepository<Requirement,String> , JpaSpecificationExecutor<Requirement> {

    Optional<Requirement> findTopByOrderByJobIdDesc();

    default Page<Requirement> allRequirements(String keyword, Pageable pageable){
         return findAll(RequirementSpecifications.allRequirementsSearch(keyword),pageable);
    }
}
