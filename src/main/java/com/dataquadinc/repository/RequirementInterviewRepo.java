package com.dataquadinc.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.dataquadinc.model.RequirementInterview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Repository
public interface RequirementInterviewRepo extends JpaRepository<RequirementInterview, String>, JpaSpecificationExecutor<RequirementInterview> {
    
    List<RequirementInterview> findByRequirementId(String requirementId);
    List<RequirementInterview> findBySubmissionId(String submissionId);
    List<RequirementInterview> findByRecruiterId(String recruiterId);
    List<RequirementInterview> findByInterviewStatus(String interviewStatus);
    List<RequirementInterview> findByClientId(String clientId);
    
    @Query("SELECT ri FROM RequirementInterview ri WHERE ri.candidateEmail = :email")
    List<RequirementInterview> findByCandidateEmail(@Param("email") String email);
    
    @Query("SELECT ri FROM RequirementInterview ri WHERE ri.candidateEmail = :email AND ri.requirementId = :requirementId")
    Optional<RequirementInterview> findByCandidateEmailAndRequirementId(@Param("email") String email, @Param("requirementId") String requirementId);
    
    @Query("SELECT ri FROM RequirementInterview ri WHERE ri.userId = :userId")
    Page<RequirementInterview> findByUserId(@Param("userId") String userId, Pageable pageable);
    
    Page<RequirementInterview> findAll(Specification<RequirementInterview> spec, Pageable pageable);

    Optional<RequirementInterview> findTopByOrderByInterviewIdDesc();
}
