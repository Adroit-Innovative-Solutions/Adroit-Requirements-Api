package com.dataquadinc.repository;

import com.dataquadinc.model.Requirement;
import com.dataquadinc.utils.RequirementSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface RequirementRepository extends JpaRepository<Requirement,String> , JpaSpecificationExecutor<Requirement> {

    Optional<Requirement> findTopByOrderByJobIdDesc();

    default Page<Requirement> allRequirements(String keyword, Map<String,Object> filters, Pageable pageable){
         return findAll(RequirementSpecifications.allRequirements(keyword,filters),pageable);
    }
    default Page<Requirement> requirementsAssignedByUser(String userId,String keyword,Map<String,Object> filters,Pageable pageable){
        return findAll(RequirementSpecifications.requirementsAssignedByUser(userId,keyword, filters),pageable);
    }
    default Page<Requirement> requirementsAssignedToUser(String userId,String keyword,Map<String,Object> filters,Pageable pageable){
        return findAll(RequirementSpecifications.requirementsAssignedToUser(userId,keyword,filters),pageable);
    }

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Requirement r " +
            "WHERE r.clientName = :clientName AND r.createdAt >= :date")
    boolean existsByClientNameAndCreatedAtAfter(@Param("clientName") String clientName,
                                                                @Param("date") LocalDateTime date);

}
