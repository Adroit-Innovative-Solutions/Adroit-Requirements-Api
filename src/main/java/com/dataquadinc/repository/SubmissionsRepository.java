package com.dataquadinc.repository;

import com.dataquadinc.model.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SubmissionsRepository extends JpaRepository<Submissions,String>, JpaSpecificationExecutor<Submissions> {


    Optional<Submissions> findTopByOrderByJobIdDesc();

    Submissions findByCandidateEmailAndJobId(String candidateEmail, String jobId);
}
