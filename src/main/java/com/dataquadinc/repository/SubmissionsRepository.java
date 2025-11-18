package com.dataquadinc.repository;

import com.dataquadinc.model.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubmissionsRepository extends JpaRepository<Submissions,String>, JpaSpecificationExecutor<Submissions> {


    Optional<Submissions> findTopByOrderByJobIdDesc();

    Submissions findByCandidateEmailAndJobId(String candidateEmail, String jobId);

    List<Submissions> findByRecruiterId(String recruiterId);
    List<Submissions> findByRecruiterIdIn(Set<String> recruiterIds);


}
