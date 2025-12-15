package com.dataquadinc.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequirementInterviewDTO {

    private String interviewId;
    private String candidateName;
    private String candidateEmail;
    private String candidateMobileNumber;
    private String clientId;
    private String clientName;
    private String clientEmail;
    private String userId;
    private String userName;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    private OffsetDateTime interviewDateTime;
    
    private Integer duration;
    private String zoomLink;
    private String externalInterviewDetails;
    private String interviewLevel;
    private String interviewStatus;
    private String recruiterId;
    private String recruiterName;
    private boolean isPlaced;
    private String internalFeedback;
    private String comments;
    private String requirementId;
    private String submissionId;
    private String createdAt;
    private String updatedAt;
}