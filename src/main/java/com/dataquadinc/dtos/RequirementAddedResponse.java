package com.dataquadinc.dtos;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequirementAddedResponse {

    private String jobId;
    private String jobTitle;
    private String clientName;
    private String assignedBy;
    private LocalDateTime createdAt;

}
