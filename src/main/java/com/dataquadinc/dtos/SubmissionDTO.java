package com.dataquadinc.dtos;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionDTO {

    private String submissionId;
    private String candidateName;
    private String candidateEmail;
    private LocalDate dob;
    private String mobileNumber;
    private String recruiterId;
    private String recruiterName;
    private String jobId;
    private String visaType;
    private String billRate;
    private String currentCTC;
    private String expectedCTC;
    private String noticePeriod;
    private String currentLocation;
    private double totalExperience;
    private double relevantExperience;
    private String qualification;
    private String communicationSkillsRating;
    private Double requiredTechnologiesRating;
    private String overallFeedback;
    private boolean relocation;
    private String employmentType;
    private String createdAt;

}
