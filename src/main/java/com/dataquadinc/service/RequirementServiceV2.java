package com.dataquadinc.service;

import com.dataquadinc.dtos.ApiResponse;
import com.dataquadinc.dtos.RequirementDTOV2;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RequirementServiceV2 {
    ApiResponse save(String userId, RequirementDTOV2 requirementDTO, MultipartFile jobDescriptionFile) throws IOException;
}
