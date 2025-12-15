package com.dataquadinc.mapper;

import com.dataquadinc.dtos.RequirementInterviewDTO;
import com.dataquadinc.model.RequirementInterview;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class RequirementInterviewMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public RequirementInterviewDTO toDTO(RequirementInterview interview) {
        if (interview == null) return null;

        return RequirementInterviewDTO.builder()
                .interviewId(interview.getInterviewId())
                .candidateName(interview.getCandidateName())
                .candidateEmail(interview.getCandidateEmail())
                .candidateMobileNumber(interview.getCandidateMobileNumber())
                .clientId(interview.getClientId())
                .clientName(interview.getClientName())
                .clientEmail(interview.getClientEmail())
                .userId(interview.getUserId())
                .userName(interview.getUserName())
                .interviewDateTime(interview.getInterviewDateTime())
                .duration(interview.getDuration())
                .zoomLink(interview.getZoomLink())
                .externalInterviewDetails(interview.getExternalInterviewDetails())
                .interviewLevel(interview.getInterviewLevel())
                .interviewStatus(interview.getInterviewStatus())
                .recruiterId(interview.getRecruiterId())
                .recruiterName(interview.getRecruiterName())
                .isPlaced(interview.isPlaced())
                .internalFeedback(interview.getInternalFeedback())
                .comments(interview.getComments())
                .requirementId(interview.getRequirementId())
                .submissionId(interview.getSubmissionId())
                .createdAt(interview.getCreatedAt() != null ? interview.getCreatedAt().format(FORMATTER) : null)
                .updatedAt(interview.getUpdatedAt() != null ? interview.getUpdatedAt().format(FORMATTER) : null)
                .build();
    }

    public RequirementInterview toEntity(RequirementInterviewDTO dto) {
        if (dto == null) return null;

        return RequirementInterview.builder()
                .interviewId(dto.getInterviewId())
                .candidateName(dto.getCandidateName())
                .candidateEmail(dto.getCandidateEmail())
                .candidateMobileNumber(dto.getCandidateMobileNumber())
                .clientId(dto.getClientId())
                .clientName(dto.getClientName())
                .clientEmail(dto.getClientEmail())
                .userId(dto.getUserId())
                .userName(dto.getUserName())
                .interviewDateTime(dto.getInterviewDateTime())
                .duration(dto.getDuration())
                .zoomLink(dto.getZoomLink())
                .externalInterviewDetails(dto.getExternalInterviewDetails())
                .interviewLevel(dto.getInterviewLevel())
                .interviewStatus(dto.getInterviewStatus())
                .recruiterId(dto.getRecruiterId())
                .recruiterName(dto.getRecruiterName())
                .isPlaced(dto.isPlaced())
                .internalFeedback(dto.getInternalFeedback())
                .comments(dto.getComments())
                .requirementId(dto.getRequirementId())
                .submissionId(dto.getSubmissionId())
                .build();
    }
}