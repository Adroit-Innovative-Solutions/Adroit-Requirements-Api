package com.dataquadinc.service.impl;

import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.mapper.SubmissionsMapper;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.model.Submissions;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.repository.SubmissionsRepository;
import com.dataquadinc.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    SubmissionsRepository submissionsRepository;
    @Autowired
    SubmissionsMapper submissionsMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CommonDocumentRepository commonDocumentRepository;


    private final String teamUrl="https://mymulya.com/users/associated-users/";
    private final String userUrl="https://mymulya.com/users/user/";

    @Override
    public SubmissionDTO createSubmission(String userId, SubmissionDTO submissionDTO, MultipartFile resume) throws IOException {

        findIsDuplicateSubmission(submissionDTO);
        Submissions submission=submissionsMapper.toEntity(submissionDTO);

        submission.setCreatedBy(userId);
        submission.setSubmissionId(generateSubmissionId());
        Submissions savedSubmission=submissionsRepository.save(submission);

        if(savedSubmission!=null){
            CommonDocument commonDocument=new CommonDocument();
            commonDocument.setCommonDocId(savedSubmission.getSubmissionId());
            commonDocument.setFileName(resume.getOriginalFilename());
            commonDocument.setSize(resume.getSize());
            commonDocument.setData(resume.getBytes());
            commonDocument.setContentType(resume.getContentType());
            commonDocument.setUploadedAt(LocalDateTime.now());
            commonDocumentRepository.save(commonDocument);
        }

        SubmissionDTO dto = submissionsMapper.toDTO(savedSubmission);
        return dto;
    }

    @Override
    public List<SubmissionDTO> getSubmission(String userId, String jobId) {

        String teamUrlL = teamUrl + userId;
        String userUrlL = userUrl + userId;

        try {
            ResponseEntity<TeamDTO> teamResponse = restTemplate.getForEntity(teamUrlL, TeamDTO.class);
            ResponseEntity<ApiResponse> userResponse = restTemplate.getForEntity(userUrlL, ApiResponse.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            UserDTO userDTO = mapper.convertValue(userResponse.getBody().getData(), UserDTO.class);

            if (userDTO.getRoles().contains("ADMIN")) {
                List<SubmissionDTO> list = submissionsRepository.findAll()
                        .stream()
                        .map(submissionsMapper::toDTO).toList();
                return list;
            }
            if (userDTO.getRoles().contains("TEAMLEAD")) {
                Set<String> collect = teamResponse.getBody().getEmployees().stream()
                        .map(r -> r.getUserId())
                        .collect(Collectors.toSet());

                List<SubmissionDTO> list = submissionsRepository.findByRecruiterIdIn(collect)
                        .stream()
                        .map(submissionsMapper::toDTO).toList();
                return list;
            }
            if (userDTO.getRoles().contains("RECRUITER")) {
                List<SubmissionDTO> list = submissionsRepository.findByRecruiterId(userId)
                        .stream()
                        .map(submissionsMapper::toDTO).toList();
                return list;
            }
            throw  new ResourceNotFoundException("User Not Found");

        } catch (Exception e) {
           throw  new ResourceNotFoundException("Exception occurs while calling external api`s.");
        }
    }

    @Override
    public List<SubmissionDTO> getSubmissionByTeamLead(String userId, String jobId) {
        List<SubmissionDTO> list = submissionsRepository.findByRecruiterId(userId)
                .stream()
                .map(submissionsMapper::toDTO).toList();
        if(list.isEmpty()){
            throw new ResourceNotFoundException("No Data Found");
        }
        else{
            return list;
        }
    }

    public String generateSubmissionId(){

        String lastSubmissionId=submissionsRepository.findTopByOrderByJobIdDesc()
                .map(Submissions::getSubmissionId)
                .orElse("SUB000000");

        int num=Integer.parseInt(lastSubmissionId.replace("SUB",""))+1;
        return String.format("SUB%06d",num);
    }
    public void findIsDuplicateSubmission(SubmissionDTO submissionDTO){

        Submissions submissions=submissionsRepository.findByCandidateEmail(submissionDTO.getCandidateEmail());
       if(submissions!=null){
           throw new ResourceNotFoundException("Candidate Already Submitted For Job ID "+ submissions.getJobId()+"Submitted By "+submissions.getRecruiterId());
       }
    }
}
