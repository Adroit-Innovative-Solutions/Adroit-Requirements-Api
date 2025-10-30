package com.dataquadinc.dtos;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmissionAddedResponse {

    private String submissionId;
    private String candidateName;
    private String jobId;
    private String createdBy;
    private String createdAt;

}
