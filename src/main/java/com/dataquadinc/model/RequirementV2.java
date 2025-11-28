package com.dataquadinc.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "requirements_us_v2")
public class RequirementV2 extends BaseEntity{

    @Id
    private String jobId;
    private String jobTitle;
    private String clientId;
    private String clientName;
    private String jobType;
    private String location;
    private String jobMode;
    private String experienceRequired;
    private String noticePeriod;
    private String qualification;
    private String billRate;
    private int noOfPositions;
    private String visaType;

    @Column(columnDefinition = "LONGTEXT")
    private String jobDescription;

    private String status;
    private String assignedById;
    private String assignedByName;
    private String remarks;

    private String submissions;
    private String interviews;

    @PrePersist
    protected void onCreate(){
        super.onCreate();
        this.status="IN PROGRESS";
    }
}
