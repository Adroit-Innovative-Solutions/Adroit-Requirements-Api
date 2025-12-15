package com.dataquadinc.service;

import com.dataquadinc.dtos.RequirementInterviewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RequirementInterviewService {
    
    RequirementInterviewDTO createInterview(String userId, RequirementInterviewDTO interviewDTO);
    RequirementInterviewDTO getInterviewById(String interviewId);
    Page<RequirementInterviewDTO> getAllInterviews(String userId, String keyword, Map<String, Object> filters, Pageable pageable);
    Page<RequirementInterviewDTO> getInterviewsByUser(String userId, String keyword, Map<String, Object> filters, Pageable pageable);
    RequirementInterviewDTO updateInterview(String interviewId, RequirementInterviewDTO interviewDTO);
    String deleteInterview(String interviewId);
    List<RequirementInterviewDTO> getInterviewsByRequirement(String requirementId);
    List<RequirementInterviewDTO> getInterviewsBySubmission(String submissionId);

}