package com.dataquadinc.controller;

import com.dataquadinc.commons.ApiResponse;
import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.RequirementInterviewDTO;
import com.dataquadinc.service.RequirementInterviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://35.188.150.92", "http://192.168.0.140:3000", "http://192.168.0.139:3000","https://mymulya.com","http://localhost:3000","http://192.168.0.135:8080","http://192.168.0.135",
        "http://182.18.177.16","http://192.168.0.139:3000"})
@Slf4j
@RestController
@RequestMapping(SystemConstants.API_BASE_PATH+"/requirements-interview")
public class RequirementInterviewController {
    
    @Autowired
    private RequirementInterviewService interviewService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<ApiResponse<RequirementInterviewDTO>> createInterview(
            @PathVariable String userId,
            @RequestBody RequirementInterviewDTO interviewDTO) {
        log.info("Creating interview for user: {}", userId);
        RequirementInterviewDTO created = interviewService.createInterview(userId, interviewDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "Interview created successfully", HttpStatus.CREATED));
    }

    @GetMapping("/get/{interviewId}")
    public ResponseEntity<ApiResponse<RequirementInterviewDTO>> getInterviewById(@PathVariable String interviewId) {
        RequirementInterviewDTO interview = interviewService.getInterviewById(interviewId);
        return ResponseEntity.ok(ApiResponse.success(interview, "Interview retrieved successfully", HttpStatus.OK));
    }

    @GetMapping("/get-all/{userId}")
    public ResponseEntity<ApiResponse<PageResponse<RequirementInterviewDTO>>> getAllInterviews(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Map<String, Object> filters) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<RequirementInterviewDTO> interviews = interviewService.getAllInterviews(userId, keyword, filters, pageable);
        PageResponse<RequirementInterviewDTO> pageResponse = new PageResponse<>(interviews);
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "Interviews retrieved successfully", HttpStatus.OK));
    }

    @GetMapping("/get-by-requirement/{requirementId}")
    public ResponseEntity<ApiResponse<List<RequirementInterviewDTO>>> getInterviewsByRequirement(@PathVariable String requirementId) {
        List<RequirementInterviewDTO> interviews = interviewService.getInterviewsByRequirement(requirementId);
        return ResponseEntity.ok(ApiResponse.success(interviews, "Requirement interviews retrieved successfully", HttpStatus.OK));
    }

    @GetMapping("/get-by-submission/{submissionId}")
    public ResponseEntity<ApiResponse<List<RequirementInterviewDTO>>> getInterviewsBySubmission(@PathVariable String submissionId) {
        List<RequirementInterviewDTO> interviews = interviewService.getInterviewsBySubmission(submissionId);
        return ResponseEntity.ok(ApiResponse.success(interviews, "Submission interviews retrieved successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{interviewId}/{userId}")
    public ResponseEntity<ApiResponse<RequirementInterviewDTO>> updateInterview(
            @PathVariable String interviewId,
            @PathVariable String userId,
            @RequestBody RequirementInterviewDTO interviewDTO) {
        interviewDTO.setUserId(userId);
        RequirementInterviewDTO updated = interviewService.updateInterview(interviewId, interviewDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Interview updated successfully", HttpStatus.OK));
    }

    @DeleteMapping("/delete/{interviewId}")
    public ResponseEntity<ApiResponse<String>> deleteInterview(@PathVariable String interviewId) {
        String result = interviewService.deleteInterview(interviewId);
        return ResponseEntity.ok(ApiResponse.success(result, "Interview deleted successfully", HttpStatus.OK));
    }
}
