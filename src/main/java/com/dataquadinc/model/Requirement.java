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
@Table(name = "requirements_us")
public class Requirement extends BaseEntity{

    @Id
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
    private String visaType;

    @Column(columnDefinition = "LONGTEXT")
    private String jobDescription;
    @Lob
    @Column(name = "job_description_blob", columnDefinition = "MEDIUMBLOB")
    private byte[] jobDescriptionBlob;

    @OneToMany(mappedBy = "requirement", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<JobRecruiter> jobRecruiters;

    private String status;
    private String assignedById;
    private String assignedByName;

    @PrePersist
    protected void onCreate(){
        super.onCreate();
        this.status="IN PROGRESS";
    }
}
