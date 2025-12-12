package com.dataquadinc.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "requirement_interview")
public class RequirementInterview {

    @Id
    private String interviewId;

    private String candidateName;
    private String candidateEmail;
    private String candidateMobileNumber;

    private String clientId;
    private String clientName;
    private String clientEmail;

    private String userId;
    private String userName;

    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Column(name = "interview_date_time")
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

    @PrePersist
    protected void onCreate(){
        this.interviewStatus="SCHEDULED";
    }
}

