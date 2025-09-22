package com.dataquadinc.repository;

import com.dataquadinc.model.JobRecruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Set;

public interface JobRecruiterRepository extends JpaRepository<JobRecruiter,Long> , JpaSpecificationExecutor<JobRecruiter> {


    Set<JobRecruiter> findByRequirementJobId(String jobId);



}
