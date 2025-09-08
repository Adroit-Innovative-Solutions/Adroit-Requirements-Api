package com.dataquadinc.repository;

import com.dataquadinc.model.JobRecruiter;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Set;

public interface JobRecruiterRepository extends JpaRepository<JobRecruiter,Long> {


    Set<JobRecruiter> findByRequirementJobId(String jobId);
}
