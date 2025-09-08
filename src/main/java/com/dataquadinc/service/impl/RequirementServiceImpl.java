package com.dataquadinc.service.impl;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.filevalidators.FileValidator;
import com.dataquadinc.mapper.RequirementMapper;
import com.dataquadinc.model.BaseEntity;
import com.dataquadinc.model.JobRecruiter;
import com.dataquadinc.model.Requirement;
import com.dataquadinc.repository.JobRecruiterRepository;
import com.dataquadinc.repository.RequirementRepository;
import com.dataquadinc.service.JobRecruiterService;
import com.dataquadinc.service.RequirementService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequirementServiceImpl implements RequirementService {

    @Autowired
    JobRecruiterRepository jobRecruiterRepository;
    @Autowired
    RequirementRepository requirementRepository;
    @Autowired
    RequirementMapper requirementMapper;
    @Autowired
    UserFeignClient userFeignClient;

    @Override
    public RequirementAddedResponse addRequirement(AddRequirementDTO dto,String userId,String jobId,MultipartFile jobDescriptionFile) throws IOException {

        Requirement requirement;
        if(jobId==null || jobId.isEmpty()) {
            requirement=requirementMapper.toEntity(dto);
            requirement.setJobId(generateJobId());
            requirement.setCreatedBy(userId);
            requirement.setAssignedBy(userId);
        }
        else {
           requirement=requirementRepository.findById(jobId)
                   .orElseThrow(()-> new ResourceNotFoundException("Requirement not found with id: " + jobId));
            // update only non-null fields from dto → requirement
            requirement.setUpdatedBy(userId);
            requirement.setAssignedBy(userId);
            requirementMapper.updateEntityFromDto(dto, requirement);

        }

        if(jobDescriptionFile!=null){
                requirement.setJobDescriptionBlob(jobDescriptionFile.getBytes());
        }
       Requirement savedRequirement=requirementRepository.save(requirement);
        addOrUpdateJobRecruiter(savedRequirement,dto.getUserIds(), savedRequirement.getAssignedBy(),userId);

       return requirementMapper.toResponse(savedRequirement);
    }

    @Override
    public Page<RequirementDTO> allRequirements(String keyword,Pageable pageable) {
       log.info("Fetching Requirements ...");
       Page<Requirement> requirementPage=requirementRepository.allRequirements(keyword, pageable);
       log.info("Fetched {} requirements with keyword {} ",requirementPage.getTotalElements(),keyword);
       Page<RequirementDTO> requirementPageDTO=requirementPage.map(requirementMapper::toDto);

        return requirementPageDTO.map(requirementDTO -> {
            requirementDTO.setAssignedUsers(getUserAssignments(requirementDTO.getJobId()));
            return requirementDTO;
        });
    }

    @Override
    public RequirementDTO requirementById(String jobId) {

      Requirement requirement=requirementRepository.findById(jobId)
               .orElseThrow(()-> new ResourceNotFoundException("No Requirement Found With Job ID "+jobId));

       RequirementDTO requirementDTO=requirementMapper.toDto(requirement);
        requirementDTO.setAssignedUsers(getUserAssignments(jobId));
       return requirementDTO;
    }

    @Override
    public ResponseEntity<byte[]> downloadJd(String jobId) {

        Requirement requirement=requirementRepository.findById(jobId).
                orElseThrow(()-> new ResourceNotFoundException("No Resource Found With ID "+jobId));
        if(requirement.getJobDescriptionBlob()==null){
            throw new ResourceNotFoundException("No JD file found for Job Id "+jobId);
        }
        byte[] jdBytes=requirement.getJobDescriptionBlob();

        Tika tika=new Tika();
        String mimeType=tika.detect(jdBytes);
        String extension=FileValidator.mapMimeTypeToExtension(mimeType);


         HttpHeaders httpHeaders=new HttpHeaders();
         httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
         httpHeaders.setContentDispositionFormData("attachment","JobDescription_"+jobId+"."+extension);

        return new ResponseEntity<>(jdBytes,httpHeaders, HttpStatus.OK);
    }

    @Override
    public DeletedRequirementResponse deleteRequirement(String jobId, String userId) {

        Requirement requirement=requirementRepository.findById(jobId)
                .orElseThrow(()-> new ResourceNotFoundException("No Requirement Found With ID "+jobId));

        Set<JobRecruiter> jobRecruiters=requirement.getJobRecruiters();
        jobRecruiters.stream()
                        .forEach(jobRecruiter -> {
                            jobRecruiter.setIsDeleted(true);
                            jobRecruiter.setDeletedAt(LocalDateTime.now());
                            jobRecruiter.setDeletedBy(userId);
                        });
//        jobRecruiterRepository.saveAll(deletedJobRecruiters);
        requirement.setJobRecruiters(jobRecruiters);
        requirement.setIsDeleted(true);
        requirement.setDeletedAt(LocalDateTime.now());
        requirement.setDeletedBy(userId);

        log.info("Requirement Is Deleted For Job ID {} deleted By {}",jobId,userId);
        Requirement savedRequirement=requirementRepository.save(requirement);

        return requirementMapper.toDeletedRequirementResponse(savedRequirement);
    }

    public List<UserAssignment> getUserAssignments(String jobId){
       Set<JobRecruiter> jobRecruiters= jobRecruiterRepository.findByRequirementJobId(jobId);

       Set<String> userIdsSet=jobRecruiters.stream()
               .map(JobRecruiter::getUserId)
               .collect(Collectors.toSet());
       List<String> userIdsList=new ArrayList<>(userIdsSet);
       return userFeignClient.getUserIdsAndUserNames(userIdsList).getBody();
    }
    public String generateJobId(){
         String lastJobId=requirementRepository.findTopByOrderByJobIdDesc()
                 .map(Requirement::getJobId)
                 .orElse("JOB000000");

         int num=Integer.parseInt(lastJobId.replace("JOB",""))+1;
         return String.format("JOB%06d",num);
    }

    @Transactional
    public Set<JobRecruiter> addOrUpdateJobRecruiter(Requirement requirement,Set<String> userIds,String assignedBy,String userID){

        //Fetch Existing Recruiters for Requirement
        Set<JobRecruiter> existingJobRecruiters=jobRecruiterRepository.findByRequirementJobId(requirement.getJobId());

        // Extracting existing userIds
        Set<String> existingUserIds=existingJobRecruiters.stream()
                .filter(jobRecruiter -> !jobRecruiter.getIsDeleted())
                .map(JobRecruiter::getUserId)
                .collect(Collectors.toSet());

        // New Recruiters to Add
        Set<JobRecruiter> toAdd=userIds.stream()
                .filter(userId-> !existingUserIds.contains(userId))
                .map(userId->{
                    JobRecruiter jobRecruiter = new JobRecruiter();
                    jobRecruiter.setUserId(userId);
                    jobRecruiter.setAssignedBy(assignedBy);
                    jobRecruiter.setCreatedBy(assignedBy);
                    jobRecruiter.setRequirement(requirement);
                    return jobRecruiter;
                }).collect(Collectors.toSet());

        // To Remove
        Set<JobRecruiter> toDelete=existingJobRecruiters.stream()
                .filter(jobRecruiter -> !userIds.contains(jobRecruiter.getUserId()))
                .collect(Collectors.toSet());

        existingJobRecruiters.stream()
                .filter(jobRecruiter -> !userIds.contains(jobRecruiter.getUserId()))
                .forEach( jobRecruiter -> {
                    jobRecruiter.setIsDeleted(true);
                    jobRecruiter.setDeletedAt(LocalDateTime.now());
                    jobRecruiter.setDeletedBy(userID);
                });

        Set<JobRecruiter> finalJobRecruiters=new HashSet<>();
        finalJobRecruiters.addAll(jobRecruiterRepository.saveAll(toAdd));
        finalJobRecruiters.addAll(jobRecruiterRepository.saveAll(existingJobRecruiters));

        return finalJobRecruiters;

    }


}
