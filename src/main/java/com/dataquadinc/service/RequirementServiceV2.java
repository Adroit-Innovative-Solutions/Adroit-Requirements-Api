package com.dataquadinc.service;

import com.dataquadinc.dtos.RequirementDTOV2;
import org.springframework.web.multipart.MultipartFile;

public interface RequirementServiceV2 {
    void save(String userId, RequirementDTOV2 requirementDTO, MultipartFile jobDescriptionFile);
}
