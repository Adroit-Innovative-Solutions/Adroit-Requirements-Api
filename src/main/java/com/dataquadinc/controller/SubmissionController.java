package com.dataquadinc.controller;

import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.ApiResponse;
import com.dataquadinc.dtos.SubmissionAddedResponse;
import com.dataquadinc.dtos.SubmissionDTO;
import com.dataquadinc.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(SystemConstants.API_BASE_PATH+"/requirements")
public class SubmissionController {

    @Autowired
    SubmissionService submissionService;

    @PostMapping("/create-submission/{userId}")
    public ResponseEntity<ApiResponse<SubmissionAddedResponse>> createSubmission(
            @PathVariable String userId,
            @ModelAttribute SubmissionDTO submissionDTO,
            @RequestParam (required = false) MultipartFile resume
            ) throws IOException {

        ApiResponse apiResponse=new ApiResponse<>(true,"Submission Created Successfully",submissionService.createSubmission(userId,submissionDTO,resume),null);

     return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/get-submission/{jobId}")
    public ResponseEntity<ApiResponse<SubmissionDTO>> getSubmission(@PathVariable String jobId){
        ApiResponse apiResponse=new ApiResponse<>(true, "Submission Fetched Successfully", submissionService.getSubmission(jobId), null);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
