package com.dataquadinc.controller;

import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.service.RequirementServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@CrossOrigin(origins = {"http://35.188.150.92", "http://192.168.0.140:3000", "http://192.168.0.139:3000","https://mymulya.com","http://localhost:3000","http://192.168.0.135:8080","http://192.168.0.135",
        "http://182.18.177.16","http://192.168.0.139:3000"})
@Slf4j
@RestController
@RequestMapping(SystemConstants.API_BASE_PATH+"/requirements/v2")
public class RequirementControllerV2 {

    @Autowired
    RequirementServiceV2 requirementServiceV2;

    @PostMapping("/post-requirement/{userId}")
    public ApiResponse postRequirement(
            @PathVariable String userId,
            @ModelAttribute RequirementReqDTOV2 requirementDTO,
            @RequestParam (value = "jobDescriptionFile",required = false) MultipartFile jobDescriptionFile) throws IOException {

        ApiResponse save = requirementServiceV2.save(userId, requirementDTO, jobDescriptionFile);
        return save;
    }

    @GetMapping("/get-requirement/{jobId}")
    public ApiResponse getRequirement(@PathVariable String jobId) {
        ApiResponse requirement = requirementServiceV2.getRequirement(jobId);
        return requirement;
    }
    
    @GetMapping("/get-requirements/{userId}")
    public PageResponse getRequirementsByUserId(
            @PathVariable String userId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam Map<String, Object> filters) {
        
        filters.remove("keyword");
        filters.remove("page");
        filters.remove("size");
        
        Pageable pageable = PageRequest.of(page, size);
        return requirementServiceV2.getRequirementByUserId(userId, keyword, pageable, filters);
    }

}
