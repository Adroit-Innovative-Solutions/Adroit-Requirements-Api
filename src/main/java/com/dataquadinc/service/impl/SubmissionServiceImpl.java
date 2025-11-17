package com.dataquadinc.service.impl;

import com.dataquadinc.dtos.ApiResponse;
import com.dataquadinc.dtos.SubmissionAddedResponse;
import com.dataquadinc.dtos.SubmissionDTO;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.mapper.SubmissionsMapper;
import com.dataquadinc.model.Submissions;
import com.dataquadinc.repository.SubmissionsRepository;
import com.dataquadinc.service.SubmissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    SubmissionsRepository submissionsRepository;
    @Autowired
    SubmissionsMapper submissionsMapper;


    @Override
    public SubmissionAddedResponse createSubmission(String userId, SubmissionDTO submissionDTO, MultipartFile resume) throws IOException {

        findIsDuplicateSubmission(submissionDTO);
        Submissions submission=submissionsMapper.toEntity(submissionDTO);

        submission.setCreatedBy(userId);
        submission.setSubmissionId(generateSubmissionId());
        if(!resume.isEmpty()){
            submission.setResume(resume.getBytes());
        }
        Submissions savedSubmission=submissionsRepository.save(submission);


        return submissionsMapper.toSubmissionAddedResponse(savedSubmission);
    }

    @Override
    public List<SubmissionAddedResponse> getSubmission(String jobId) {
        return List.of();
    }

    public String generateSubmissionId(){

        String lastSubmissionId=submissionsRepository.findTopByOrderByJobIdDesc()
                .map(Submissions::getSubmissionId)
                .orElse("SUB000000");

        int num=Integer.parseInt(lastSubmissionId.replace("SUB",""))+1;
        return String.format("SUB%06d",num);
    }
    public void findIsDuplicateSubmission(SubmissionDTO submissionDTO){

        Submissions submissions=submissionsRepository.findByCandidateEmailAndJobId(submissionDTO.getCandidateEmail(),submissionDTO.getJobId());
       if(submissions!=null){
           throw new ResourceNotFoundException("Candidate Already Submitted For Job ID "+ submissions.getJobId()+"Submitted By "+submissions.getRecruiterId());
       }
    }
}
