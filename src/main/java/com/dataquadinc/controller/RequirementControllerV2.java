package com.dataquadinc.controller;

import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.service.RequirementServiceV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
            @ModelAttribute RequirementDTOV2 requirementDTO,
            @RequestParam (value = "jobDescriptionFile",required = false) MultipartFile jobDescriptionFile) throws IOException {

        com.dataquadinc.dtos.ApiResponse save = requirementServiceV2.save(userId, requirementDTO, jobDescriptionFile);
        return save;
    }

}
