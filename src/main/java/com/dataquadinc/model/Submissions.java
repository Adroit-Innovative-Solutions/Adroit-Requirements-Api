package com.dataquadinc.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "submissions_us")
public class Submissions extends BaseEntity{

    @Id
    private String submissionId;
    private String candidateName;
    private String candidateEmail;
    private String mobileNumber;
    private LocalDate dob;
    private String visaType;
    private String billRate;
    private String payRate;
    private String noticePeriod;
    private String currentLocation;
    private boolean relocation;
    private double totalExperience;
    private double relevantExperience;
    private String qualification;
    private String employmentType;
    @Column(name = "confirm_rtr")
    private String confirmRTR;
    @Column(name = "overallFeedback", columnDefinition = "TEXT")
    private String overallFeedback;
    private String recruiterId;
    private String recruiterName;
    private String jobId;
}
