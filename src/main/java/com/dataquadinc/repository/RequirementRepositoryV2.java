package com.dataquadinc.repository;

import com.dataquadinc.model.Requirement;
import com.dataquadinc.model.RequirementV2;
import com.dataquadinc.utils.RequirementSpecificationsV2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

public interface RequirementRepositoryV2 extends JpaRepository<RequirementV2,String> , JpaSpecificationExecutor<RequirementV2> {

    Optional<Requirement> findTopByOrderByJobIdDesc();

    default Page<RequirementV2> allRequirements(String keyword, Map<String,Object> filters, Pageable pageable){
         return findAll(RequirementSpecificationsV2.allRequirements(keyword,filters),pageable);
    }
    default Page<RequirementV2> requirementsAssignedByUser(String userId, String keyword, Map<String,Object> filters, Pageable pageable){
        return findAll(RequirementSpecificationsV2.requirementsAssignedByUser(userId,keyword, filters),pageable);
    }
    default Page<RequirementV2> requirementsAssignedToUser(String userId, String keyword, Map<String,Object> filters, Pageable pageable){
        return findAll(RequirementSpecificationsV2.requirementsAssignedToUser(userId,keyword,filters),pageable);
    }

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Requirement r " +
            "WHERE r.clientName = :clientName AND r.createdAt >= :date")
    boolean existsByClientNameAndCreatedAtAfter(@Param("clientName") String clientName,
                                                                @Param("date") LocalDateTime date);

}
