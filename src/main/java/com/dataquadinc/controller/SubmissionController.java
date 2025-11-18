package com.dataquadinc.controller;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(SystemConstants.API_BASE_PATH + "/requirements")
public class SubmissionController {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    UserFeignClient userFeignClient;

    @PostMapping("/create-submission/{userId}")
    public ResponseEntity<ApiResponse<SubmissionAddedResponse>> createSubmission(
            @PathVariable String userId,
            @ModelAttribute SubmissionDTO submissionDTO,
            @RequestParam(required = false) MultipartFile resume
    ) throws IOException {

        ApiResponse apiResponse = new ApiResponse<>(true, "Submission Created Successfully", submissionService.createSubmission(userId, submissionDTO, resume), null);

        return new ResponseEntity<>(apiResponse, HttpStatus.CREATED);
    }

    @GetMapping("/get-submission/{userId}/{jobId}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionForAdmin(@PathVariable String userId, @PathVariable String jobId) {
        List<SubmissionDTO> submission = submissionService.getSubmission(userId, jobId);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    @GetMapping("/get-submission/self/{userId}/{jobId}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionForTeamLead(@PathVariable String userId, @PathVariable String jobId) {
        List<SubmissionDTO> submissionByTeamLead = submissionService.getSubmissionByTeamLead(userId, jobId);
        return new ResponseEntity<>(submissionByTeamLead, HttpStatus.OK);
    }
}
