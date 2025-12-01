package com.dataquadinc.service.impl;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.dtos.ApiResponse;
import com.dataquadinc.dtos.RequirementDTOV2;
import com.dataquadinc.dtos.UserAssignment;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.model.JobRecruiterV2;
import com.dataquadinc.model.Requirement;
import com.dataquadinc.model.RequirementV2;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.repository.JobRecruiterRepositoryV2;
import com.dataquadinc.repository.RequirementRepositoryV2;
import com.dataquadinc.service.RequirementServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RequirementServiceImplV2 implements RequirementServiceV2 {

    @Autowired
    JobRecruiterRepositoryV2 jobRecruiterRepositoryV2;
    @Autowired
    RequirementRepositoryV2 requirementRepositoryV2;

    @Autowired
    CommonDocumentRepository commonDocumentRepository;

    @Autowired
    UserFeignClient userFeignClient;


    @Override
    public ApiResponse save(String userId, RequirementDTOV2 requirementDTO, MultipartFile jobDescriptionFile) throws IOException {
        RequirementV2 requirement = new RequirementV2();

        requirement.setJobId(generateJobId());
        requirement.setJobTitle(requirementDTO.getJobTitle());
        requirement.setClientId(requirementDTO.getClientId());
        requirement.setClientName(requirementDTO.getClientName());
        requirement.setJobType(requirementDTO.getJobType());
        requirement.setLocation(requirementDTO.getLocation());
        requirement.setJobMode(requirementDTO.getJobMode());
        requirement.setExperienceRequired(requirementDTO.getExperienceRequired());
        requirement.setNoticePeriod(requirementDTO.getNoticePeriod());
        requirement.setQualification(requirementDTO.getQualification());
        requirement.setNoOfPositions(requirementDTO.getNoOfPositions());
        requirement.setVisaType(requirementDTO.getVisaType());

        // LONGTEXT field
        requirement.setJobDescription(requirementDTO.getJobDescription());

        // Newly added fields you were missing
        requirement.setBillRate(requirementDTO.getBillRate());
        requirement.setRemarks(requirementDTO.getRemarks());
        // Assignment info
        requirement.setAssignedById(userId);
        requirement.setAssignedByName(getUserNameFromUserId(userId));

        // BaseEntity fields (if BaseEntity has createdBy)
        requirement.setCreatedBy(userId);

        RequirementV2 save = requirementRepositoryV2.save(requirement);

        if (save!=null){
            requirementDTO.getAssignedUsers()
                    .forEach(user->{
                        JobRecruiterV2 jobRecruiter = new JobRecruiterV2();
                        jobRecruiter.setUserId(user);
                        jobRecruiter.setUserName(getUserNameFromUserId(user));
                        jobRecruiter.setRequirementId(save.getJobId());
                        jobRecruiterRepositoryV2.save(jobRecruiter);
                    });
        }

        if (jobDescriptionFile!=null&&save!=null){
            CommonDocument commonDocument = new CommonDocument();
            commonDocument.setCommonDocId(save.getJobId());
            commonDocument.setFileName(jobDescriptionFile.getOriginalFilename());
            commonDocument.setSize(jobDescriptionFile.getSize());
            commonDocument.setData(jobDescriptionFile.getBytes());
            commonDocument.setContentType(jobDescriptionFile.getContentType());
            commonDocument.setUploadedAt(LocalDateTime.now());
            CommonDocument save1 = commonDocumentRepository.save(commonDocument);
        }

        if (save!=null){
            log.info("Requirement Saved Successfully For Job ID {}",save.getJobId());
        }

        ApiResponse apiResponse =new ApiResponse<>(true,"Requirement Saved Successfully",save.getJobId(),null);

        return apiResponse;

    }



    public String generateJobId(){
        String lastJobId=requirementRepositoryV2.findTopByOrderByJobIdDesc()
                .map(Requirement::getJobId)
                .orElse("JOB000000");

        int num=Integer.parseInt(lastJobId.replace("JOB",""))+1;
        return String.format("JOB%06d",num);
    }

    public String getUserNameFromUserId(String userId){
        List<UserAssignment> user=userFeignClient.getUserIdsAndUserNames(List.of(userId)).getBody();
        return user.getFirst().getUserName();
    }
}
