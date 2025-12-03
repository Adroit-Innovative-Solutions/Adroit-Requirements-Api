package com.dataquadinc.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequirementResDTOV2 {

    private String jobId;
    private String jobTitle;
    private String clientId;
    private String clientName;
    private String jobType;
    private String location;
    private String jobMode;
    private String experienceRequired;
    private String noticePeriod;
    private String relevantExperience;
    private String qualification;
    private int noOfPositions;
    private String jobDescription;
    private String status;
    private String visaType;
    private String assignedById;
    private String assignedByName;
    private String billRate;
    private String remarks;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String submissions;
    private String interviews;

    private List<JobRecruiterDto> assignedUsers;
    private Set<String> teamsLeadIds;
}
