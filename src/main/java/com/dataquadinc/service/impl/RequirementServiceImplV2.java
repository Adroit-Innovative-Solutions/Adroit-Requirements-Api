package com.dataquadinc.service.impl;

import com.dataquadinc.dtos.RequirementDTOV2;
import com.dataquadinc.model.Requirement;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.repository.JobRecruiterRepositoryV2;
import com.dataquadinc.repository.RequirementRepositoryV2;
import com.dataquadinc.service.RequirementServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class RequirementServiceImplV2 implements RequirementServiceV2 {

    @Autowired
    JobRecruiterRepositoryV2 jobRecruiterRepositoryV2;
    @Autowired
    RequirementRepositoryV2 requirementRepositoryV2;

    @Autowired
    CommonDocumentRepository commonDocumentRepository;

    @Override
    public void save(String userId, RequirementDTOV2 requirementDTO, MultipartFile jobDescriptionFile) {




    }
    public String generateJobId(){
        String lastJobId=requirementRepositoryV2.findTopByOrderByJobIdDesc()
                .map(Requirement::getJobId)
                .orElse("JOB000000");

        int num=Integer.parseInt(lastJobId.replace("JOB",""))+1;
        return String.format("JOB%06d",num);
    }
}
