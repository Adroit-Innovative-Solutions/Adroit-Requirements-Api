package com.dataquadinc.service.impl;

import com.dataquadinc.repository.JobRecruiterRepository;
import com.dataquadinc.service.JobRecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobRecruiterServiceImpl implements JobRecruiterService {

    @Autowired
    JobRecruiterRepository jobRecruiterRepository;


}
