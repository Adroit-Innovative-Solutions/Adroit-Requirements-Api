package com.dataquadinc.service;

import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.dtos.ApiResponse;
import com.dataquadinc.dtos.RequirementReqDTOV2;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface RequirementServiceV2 {
    ApiResponse save(String userId, RequirementReqDTOV2 requirementDTO, MultipartFile jobDescriptionFile) throws IOException;

    ApiResponse getRequirement(String jobId);
    
    PageResponse getRequirementByUserId(String userId, String keyword, Pageable pageable, Map<String, Object> filters);
}
