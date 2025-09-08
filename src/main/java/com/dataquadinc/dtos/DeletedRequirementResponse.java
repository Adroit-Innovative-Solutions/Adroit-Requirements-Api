package com.dataquadinc.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class DeletedRequirementResponse {

    private String jobId;
    private String jobTitle;
    private String clientName;
    private String deletedAt;
    private String deletedBy;

}
