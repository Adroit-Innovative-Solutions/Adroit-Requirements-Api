package com.dataquadinc.controller;

import com.dataquadinc.commons.ApiResponse;
import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.service.RequirementService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    RequirementService requirementService;

    @PostMapping("/post-requirement/{userId}")
    public ResponseEntity<ApiResponse<RequirementAddedResponse>> postRequirement(
            @PathVariable String userId,
            @ModelAttribute RequirementDTOV2 requirementDTO,
            @RequestParam (required = false) String jobId,
            @RequestParam (value = "jobDescriptionFile",required = false) MultipartFile jobDescriptionFile) throws IOException {


        return null;
    }

}
