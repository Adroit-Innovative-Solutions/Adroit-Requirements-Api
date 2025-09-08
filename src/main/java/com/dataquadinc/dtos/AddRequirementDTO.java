package com.dataquadinc.dtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddRequirementDTO {

    private String jobId;
    private String jobTitle;
    private String clientName;
    private String jobType;
    private String location;
    private String jobMode;
    private String experienceRequired;
    private String noticePeriod;
    private String relevantExperience;
    private String qualification;
    private String salaryPackage;
    private int noOfPositions;
    private String jobDescription;
    private String status;
    private String visaType;
    private String assignedBy;
    private Set<String> userIds;

}
