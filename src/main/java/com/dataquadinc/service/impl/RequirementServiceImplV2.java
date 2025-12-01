package com.dataquadinc.service.impl;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.GlobalException;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.model.JobRecruiterV2;
import com.dataquadinc.model.RequirementV2;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.repository.JobRecruiterRepositoryV2;
import com.dataquadinc.repository.RequirementRepositoryV2;
import com.dataquadinc.service.RequirementServiceV2;
import com.dataquadinc.utils.RequirementSpecificationsV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

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
    
    @Autowired
    RestTemplate restTemplate;
    
    private final String teamUrl = "https://mymulya.com/users/associated-users/";
    private final String userUrl = "https://mymulya.com/users/user/";


    @Override
    @Transactional
    public ApiResponse save(String userId, RequirementReqDTOV2 requirementDTO, MultipartFile jobDescriptionFile) throws IOException {

        requirementRepositoryV2.findByClientIdAndJobTitleAndExperienceRequired(
                requirementDTO.getClientId(),
                requirementDTO.getJobTitle(),
                requirementDTO.getExperienceRequired()
        ).ifPresent(requirement -> {
            throw new GlobalException("Requirement already exists with the same client, job title, and experience required.");
        });

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

        if (save != null && requirementDTO.getAssignedUsers() != null && requirementDTO.getAssignedUsers().size() > 0) {
            requirementDTO.getAssignedUsers()
                    .forEach(user -> {
                        JobRecruiterV2 jobRecruiter = new JobRecruiterV2();
                        jobRecruiter.setUserId(user);
                        jobRecruiter.setUserName(getUserNameFromUserId(user));
                        jobRecruiter.setRequirementId(save.getJobId());
                        jobRecruiterRepositoryV2.save(jobRecruiter);
                    });
        }

        if (jobDescriptionFile != null && save != null) {
            CommonDocument commonDocument = new CommonDocument();
            commonDocument.setCommonDocId(save.getJobId());
            commonDocument.setFileName(jobDescriptionFile.getOriginalFilename());
            commonDocument.setSize(jobDescriptionFile.getSize());
            commonDocument.setData(jobDescriptionFile.getBytes());
            commonDocument.setContentType(jobDescriptionFile.getContentType());
            commonDocument.setUploadedAt(LocalDateTime.now());
            CommonDocument save1 = commonDocumentRepository.save(commonDocument);
        }

        if (save != null) {
            log.info("Requirement Saved Successfully For Job ID {}", save.getJobId());
        }

        ApiResponse apiResponse = new ApiResponse<>(true, "Requirement Saved Successfully", save.getJobId(), null);

        return apiResponse;

    }

    @Override
    public ApiResponse getRequirement(String jobId) {
        ApiResponse apiResponse = new ApiResponse();
        RequirementV2 requirement = requirementRepositoryV2.findById(jobId)
                .orElseThrow(() -> new GlobalException("No Requirement Found With ID " + jobId));

        RequirementResDTOV2 requirementResDTOV2 = new RequirementResDTOV2();
        requirementResDTOV2.setJobId(requirement.getJobId());
        requirementResDTOV2.setJobTitle(requirement.getJobTitle());
        requirementResDTOV2.setClientId(requirement.getClientId());
        requirementResDTOV2.setClientName(requirement.getClientName());
        requirementResDTOV2.setJobType(requirement.getJobType());
        requirementResDTOV2.setLocation(requirement.getLocation());
        requirementResDTOV2.setJobMode(requirement.getJobMode());
        requirementResDTOV2.setExperienceRequired(requirement.getExperienceRequired());
        requirementResDTOV2.setNoticePeriod(requirement.getNoticePeriod());
        requirementResDTOV2.setQualification(requirement.getQualification());
        requirementResDTOV2.setNoOfPositions(requirement.getNoOfPositions());
        requirementResDTOV2.setVisaType(requirement.getVisaType());
        requirementResDTOV2.setJobDescription(requirement.getJobDescription());
        requirementResDTOV2.setBillRate(requirement.getBillRate());
        requirementResDTOV2.setRemarks(requirement.getRemarks());
        requirementResDTOV2.setCreatedAt(requirement.getCreatedAt());
        requirementResDTOV2.setUpdatedAt(requirement.getUpdatedAt());
        requirementResDTOV2.setAssignedById(requirement.getAssignedById());
        requirementResDTOV2.setAssignedByName(requirement.getAssignedByName());
        requirementResDTOV2.setStatus(requirement.getStatus());
        List<JobRecruiterDto> jobRecruiterDto = new ArrayList<JobRecruiterDto>();
        List<JobRecruiterV2> byRequirementId = jobRecruiterRepositoryV2.findByRequirementId(jobId);
        byRequirementId.forEach(jobRecruiter -> {
            JobRecruiterDto jobRecruiterDto1 = new JobRecruiterDto();
            jobRecruiterDto1.setUserId(jobRecruiter.getUserId());
            jobRecruiterDto1.setUserName(jobRecruiter.getUserName());
            jobRecruiterDto.add(jobRecruiterDto1);
        });
        requirementResDTOV2.setAssignedUsers(jobRecruiterDto);
        requirementResDTOV2.setInterviews(requirement.getInterviews());
        requirementResDTOV2.setSubmissions(requirement.getSubmissions());


        apiResponse.setSuccess(true);
        apiResponse.setMessage("Requirement Found");
        apiResponse.setData(requirementResDTOV2);
        return apiResponse;
    }

    public String generateJobId() {
        String lastJobId = requirementRepositoryV2.findTopByOrderByJobIdDesc()
                .map(RequirementV2::getJobId)
                .orElse("JOB000000");

        int num = Integer.parseInt(lastJobId.replace("JOB", "")) + 1;
        return String.format("JOB%06d", num);
    }

    public String getUserNameFromUserId(String userId) {
        List<UserAssignment> user = userFeignClient.getUserIdsAndUserNames(List.of(userId)).getBody();
        if (user == null || user.isEmpty()) {
            throw new GlobalException("Recruiters ids are not correct");
        }
        return user.getFirst().getUserName();
    }
    
    public PageResponse getRequirementByUserId(String userId, String keyword, Pageable pageable, Map<String, Object> filters) {
        String teamUrlL = teamUrl + userId;
        String userUrlL = userUrl + userId;
        
        try {
            ResponseEntity<TeamDTO> teamResponse = restTemplate.getForEntity(teamUrlL, TeamDTO.class);
            ResponseEntity<ApiResponse> userResponse = restTemplate.getForEntity(userUrlL, ApiResponse.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            
            UserDTO userDTO = mapper.convertValue(userResponse.getBody().getData(), UserDTO.class);
            
            Specification<RequirementV2> spec;
            
            if (userDTO.getRoles().contains("SUPERADMIN")) {
                spec = RequirementSpecificationsV2.allRequirements(keyword, filters);
            } else if (userDTO.getRoles().contains("TEAMLEAD")) {
                spec = RequirementSpecificationsV2.requirementsAssignedByUser(userId, keyword, filters);
            } else if (userDTO.getRoles().contains("RECRUITER")) {
                spec = RequirementSpecificationsV2.requirementsAssignedToUser(userId, keyword, filters);
            } else {
                throw new GlobalException("User doesn't have any submissions");
            }
            
            Page<RequirementV2> requirements = requirementRepositoryV2.findAll(spec, pageable);
            
            Page<RequirementResDTOV2> requirementDTOs = requirements.map(requirement -> {
                RequirementResDTOV2 dto = new RequirementResDTOV2();
                dto.setJobId(requirement.getJobId());
                dto.setJobTitle(requirement.getJobTitle());
                dto.setClientId(requirement.getClientId());
                dto.setClientName(requirement.getClientName());
                dto.setJobType(requirement.getJobType());
                dto.setLocation(requirement.getLocation());
                dto.setJobMode(requirement.getJobMode());
                dto.setExperienceRequired(requirement.getExperienceRequired());
                dto.setNoticePeriod(requirement.getNoticePeriod());
                dto.setQualification(requirement.getQualification());
                dto.setNoOfPositions(requirement.getNoOfPositions());
                dto.setVisaType(requirement.getVisaType());
                dto.setJobDescription(requirement.getJobDescription());
                dto.setBillRate(requirement.getBillRate());
                dto.setRemarks(requirement.getRemarks());
                dto.setCreatedAt(requirement.getCreatedAt());
                dto.setUpdatedAt(requirement.getUpdatedAt());
                dto.setAssignedById(requirement.getAssignedById());
                dto.setAssignedByName(requirement.getAssignedByName());
                dto.setStatus(requirement.getStatus());
                dto.setInterviews(requirement.getInterviews());
                dto.setSubmissions(requirement.getSubmissions());
                
                List<JobRecruiterDto> assignedUsers = jobRecruiterRepositoryV2.findByRequirementId(requirement.getJobId())
                    .stream()
                    .map(jr -> {
                        JobRecruiterDto jrDto = new JobRecruiterDto();
                        jrDto.setUserId(jr.getUserId());
                        jrDto.setUserName(jr.getUserName());
                        return jrDto;
                    })
                    .collect(Collectors.toList());
                dto.setAssignedUsers(assignedUsers);
                
                return dto;
            });
            
            return PageResponse.of(requirementDTOs);
            
        } catch (Exception e) {
            throw new GlobalException("Exception occurs while calling external APIs: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ApiResponse delete(String jobId){
        Optional<RequirementV2> requirement = requirementRepositoryV2.findById(jobId);

        if(requirement.isEmpty()){
            return new ApiResponse(false, "Requirement not found", null,null);
        }
        requirementRepositoryV2.deleteById(jobId);
        jobRecruiterRepositoryV2.deleteByRequirementId(jobId);
        commonDocumentRepository.deleteByCommonDocId(jobId);

        return new ApiResponse(true, "Requirement deleted successfully", jobId, null);
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadJobDescription(String jobId) {
        CommonDocument commonDocument = commonDocumentRepository.findByCommonDocId(jobId);
        if (commonDocument == null) {
            log.error("Job Description not found for Job ID {}", jobId);
            throw new RuntimeException("Job description file not found for Job ID: " + jobId);
        }

        try {
            ByteArrayResource resource = new ByteArrayResource(commonDocument.getData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(commonDocument.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + commonDocument.getFileName() + "\"")
                    .contentLength(commonDocument.getSize())
                    .body(resource);
        } catch (Exception e) {
            log.error("Error downloading job description for Job ID {}: {}", jobId, e.getMessage());
            throw new RuntimeException("Error downloading job description: " + e.getMessage());
        }
    }
}
