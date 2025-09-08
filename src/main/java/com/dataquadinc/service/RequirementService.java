package com.dataquadinc.service;

import com.dataquadinc.dtos.AddRequirementDTO;
import com.dataquadinc.dtos.DeletedRequirementResponse;
import com.dataquadinc.dtos.RequirementAddedResponse;
import com.dataquadinc.dtos.RequirementDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RequirementService {


    RequirementAddedResponse addRequirement(AddRequirementDTO dto, String userId,String jobId,
                                            MultipartFile jobDescriptionFile) throws IOException;

    Page<RequirementDTO> allRequirements(String keyword,Pageable pageable);

    RequirementDTO requirementById(String jobId);

    ResponseEntity<byte[]> downloadJd(String jobId);

    DeletedRequirementResponse deleteRequirement(String jobId,String userId);
}
