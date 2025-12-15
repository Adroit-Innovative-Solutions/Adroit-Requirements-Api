package com.dataquadinc.service.impl;

import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.mapper.RequirementInterviewMapper;
import com.dataquadinc.model.RequirementInterview;
import com.dataquadinc.repository.RequirementInterviewRepo;
import com.dataquadinc.service.RequirementInterviewService;
import com.dataquadinc.utils.RequirementInterviewSpecification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RequirementInterviewServiceImpl implements RequirementInterviewService {

    @Autowired
    private RequirementInterviewRepo interviewRepo;

    @Autowired
    private RequirementInterviewMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public RequirementInterviewDTO createInterview(String userId, RequirementInterviewDTO interviewDTO) {
        log.info("Creating interview for user: {}", userId);
        
        checkDuplicateInterview(interviewDTO);
        
        RequirementInterview interview = mapper.toEntity(interviewDTO);
        interview.setInterviewId(generateInterviewId());
        interview.setUserId(userId);
        interview.setCreatedBy(userId);
        
        RequirementInterview saved = interviewRepo.save(interview);
        return mapper.toDTO(saved);
    }

    @Override
    public RequirementInterviewDTO getInterviewById(String interviewId) {
        RequirementInterview interview = interviewRepo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
        return mapper.toDTO(interview);
    }

    @Override
    public Page<RequirementInterviewDTO> getAllInterviews(String userId, String keyword, Map<String, Object> filters, Pageable pageable) {
        String teamUrl = "https://mymulya.com/users/associated-users/" + userId;
        String userUrl = "https://mymulya.com/users/user/" + userId;
        
        try {
            ResponseEntity<TeamDTO> teamResponse = restTemplate.getForEntity(teamUrl, TeamDTO.class);
            ResponseEntity<ApiResponse> userResponse = restTemplate.getForEntity(userUrl, ApiResponse.class);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            
            UserDTO userDTO = objectMapper.convertValue(userResponse.getBody().getData(), UserDTO.class);
            
            Specification<RequirementInterview> spec = RequirementInterviewSpecification.filterByCriteria(keyword, filters);
            
            if (userDTO.getRoles().contains("SUPERADMIN")) {
                return interviewRepo.findAll(spec, pageable).map(mapper::toDTO);
            }
            
            if (userDTO.getRoles().contains("TEAMLEAD")) {
                Set<String> teamUserIds = teamResponse.getBody().getRecruiters().stream()
                        .map(r -> r.getUserId())
                        .collect(Collectors.toSet());
                teamUserIds.add(userId);
                
                Specification<RequirementInterview> teamSpec = spec.and((root, query, criteriaBuilder) -> 
                        root.get("userId").in(teamUserIds));
                return interviewRepo.findAll(teamSpec, pageable).map(mapper::toDTO);
            }
            
            if (userDTO.getRoles().contains("RECRUITER")) {
                Specification<RequirementInterview> recruiterSpec = spec.and((root, query, criteriaBuilder) -> 
                        criteriaBuilder.equal(root.get("userId"), userId));
                return interviewRepo.findAll(recruiterSpec, pageable).map(mapper::toDTO);
            }
            
            throw new ResourceNotFoundException("User doesn't have access to interviews");
            
        } catch (Exception e) {
            log.error("Error calling external APIs: {}", e.getMessage());
            throw new ResourceNotFoundException("Error accessing user data");
        }
    }

    @Override
    public Page<RequirementInterviewDTO> getInterviewsByUser(String userId, String keyword, Map<String, Object> filters, Pageable pageable) {
        Specification<RequirementInterview> spec = RequirementInterviewSpecification.filterByCriteria(keyword, filters)
                .and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId));
        Page<RequirementInterview> interviews = interviewRepo.findAll(spec, pageable);
        return interviews.map(mapper::toDTO);
    }

    @Override
    public RequirementInterviewDTO updateInterview(String interviewId, RequirementInterviewDTO interviewDTO) {
        RequirementInterview existing = interviewRepo.findById(interviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found with id: " + interviewId));
        
        existing.setUpdatedBy(interviewDTO.getUserId());
        existing.setCandidateName(interviewDTO.getCandidateName());
        existing.setCandidateEmail(interviewDTO.getCandidateEmail());
        existing.setCandidateMobileNumber(interviewDTO.getCandidateMobileNumber());
        existing.setClientId(interviewDTO.getClientId());
        existing.setClientName(interviewDTO.getClientName());
        existing.setClientEmail(interviewDTO.getClientEmail());
        existing.setInterviewDateTime(interviewDTO.getInterviewDateTime());
        existing.setDuration(interviewDTO.getDuration());
        existing.setZoomLink(interviewDTO.getZoomLink());
        existing.setExternalInterviewDetails(interviewDTO.getExternalInterviewDetails());
        existing.setInterviewLevel(interviewDTO.getInterviewLevel());
        existing.setInterviewStatus(interviewDTO.getInterviewStatus());
        existing.setRecruiterId(interviewDTO.getRecruiterId());
        existing.setRecruiterName(interviewDTO.getRecruiterName());
        existing.setPlaced(interviewDTO.isPlaced());
        existing.setInternalFeedback(interviewDTO.getInternalFeedback());
        existing.setComments(interviewDTO.getComments());
        
        RequirementInterview updated = interviewRepo.save(existing);
        return mapper.toDTO(updated);
    }

    @Override
    public String deleteInterview(String interviewId) {
        if (!interviewRepo.existsById(interviewId)) {
            throw new ResourceNotFoundException("Interview not found with id: " + interviewId);
        }
        interviewRepo.deleteById(interviewId);
        return "Interview deleted successfully";
    }

    @Override
    public List<RequirementInterviewDTO> getInterviewsByRequirement(String requirementId) {
        List<RequirementInterview> interviews = interviewRepo.findByRequirementId(requirementId);
        return interviews.stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<RequirementInterviewDTO> getInterviewsBySubmission(String submissionId) {
        List<RequirementInterview> interviews = interviewRepo.findBySubmissionId(submissionId);
        return interviews.stream().map(mapper::toDTO).collect(Collectors.toList());
    }


    private void checkDuplicateInterview(RequirementInterviewDTO interviewDTO) {
        Optional<RequirementInterview> existing = interviewRepo.findByCandidateEmailAndRequirementId(
                interviewDTO.getCandidateEmail(), interviewDTO.getRequirementId());
        if (existing.isPresent()) {
            throw new ResourceNotFoundException("Interview already exists for candidate " + 
                    interviewDTO.getCandidateEmail() + " for requirement " + interviewDTO.getRequirementId());
        }
    }

    public String generateInterviewId(){
        String lastRtrId=interviewRepo.findTopByOrderByInterviewIdDesc()
                .map(RequirementInterview::getInterviewId)
                .orElse("INTER000000");
        int num=Integer.parseInt(lastRtrId.replace("INTER",""))+1;
        return String.format("INTER%06d",num);
    }
}
