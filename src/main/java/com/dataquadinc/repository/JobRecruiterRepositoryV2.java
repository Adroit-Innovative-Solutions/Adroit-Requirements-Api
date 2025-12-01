package com.dataquadinc.repository;

import com.dataquadinc.model.JobRecruiter;
import com.dataquadinc.model.JobRecruiterV2;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

public interface JobRecruiterRepositoryV2 extends JpaRepository<JobRecruiterV2,Long> , JpaSpecificationExecutor<JobRecruiterV2> {
    Set<JobRecruiter> findByRequirementJobId(String jobId);
}
