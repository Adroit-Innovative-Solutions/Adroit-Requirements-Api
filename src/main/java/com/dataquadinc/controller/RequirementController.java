package com.dataquadinc.controller;

import com.dataquadinc.commons.ApiResponse;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.AddRequirementDTO;
import com.dataquadinc.dtos.DeletedRequirementResponse;
import com.dataquadinc.dtos.RequirementAddedResponse;
import com.dataquadinc.dtos.RequirementDTO;
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
@CrossOrigin(origins = {"http://35.188.150.92", "http://192.168.0.140:3000", "http://192.168.0.139:3000","https://mymulya.com","http://localhost:3000","http://192.168.0.135:8080","http://192.168.0.135",
        "http://182.18.177.16","http://192.168.0.139:3000"})
@Slf4j
@RestController
@RequestMapping(SystemConstants.API_BASE_PATH+"/requirements")
public class RequirementController {

    @Autowired
    RequirementService requirementService;


    /**
     * Create or update a job requirement.
     * <p>
     * Real-time use:
     * <ul>
     *   <li><b>Add:</b> Recruiter posts a new requirement (e.g., Java Developer),
     *       the system generates a requirement ID, and candidates can be linked.</li>
     *   <li><b>Update:</b> Recruiter updates an existing requirement by passing the jobId.</li>
     * </ul>
     * </p>
     *
     * @param userId recruiter/hiring manager ID
     * @param requirementDTO job/requirement details
     * @param jobId optional jobId (if provided, updates the existing requirement)
     * @param jobDescriptionFile optional job description file (PDF/DOC)
     * @return created or updated requirement response
     */
    @Operation(
            summary = "Create or Update Requirement",
            description = "Adds a new job requirement or updates an existing one.<br/>" +
                    "- If <b>jobId</b> is not provided → a new requirement is created.<br/>" +
                    "- If <b>jobId</b> is provided → the existing requirement is updated."
    )
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "201", description = "Requirement successfully created or updated",
//                    content = @Content(mediaType = "application/json",
//                            schema = @Schema(implementation = RequirementAddedResponse.class))),
//            @ApiResponse(responseCode = "404", description = "Requirement not found for update"),
//            @ApiResponse(responseCode = "400", description = "Invalid request payload")
//    })
    @PostMapping("/post-requirement/{userId}")
    public ResponseEntity<ApiResponse<RequirementAddedResponse>> postRequirement(
            @PathVariable String userId,
            @ModelAttribute AddRequirementDTO requirementDTO,
            @RequestParam (required = false) String jobId,
            @RequestParam (value = "jobDescriptionFile",required = false)MultipartFile jobDescriptionFile) throws IOException {

       log.info("Incoming Request For adding Requirement {}",userId);
        RequirementAddedResponse response =requirementService.addRequirement(requirementDTO,userId,jobId,jobDescriptionFile);
        if(jobId==null || jobId.isEmpty())
            return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(response,"Requirement created Successfully",HttpStatus.CREATED));
        else
            return ResponseEntity.status(HttpStatus.OK).body(
                    ApiResponse.success(response,"Requirement Updated Successfully",HttpStatus.OK));
    }

    @GetMapping("/allRequirements")
    public ResponseEntity<ApiResponse<Page<RequirementDTO>>> allRequirements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword
    ){
        log.info("Incoming Request For fetching All Requirement");
        Pageable pageable= PageRequest.of(page,size, Sort.Direction.DESC,"updatedAt");
        Page<RequirementDTO> response=requirementService.allRequirements(keyword,pageable);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(response,"Requirement Fetched Successfully",HttpStatus.OK));
    }

    @GetMapping("/requirement-id/{jobId}")
    public ResponseEntity<ApiResponse<RequirementDTO>> requirementById(
            @PathVariable String jobId
    ){
        log.info("Incoming Request For fetching Requirement for JobId {}",jobId);
         RequirementDTO response=requirementService.requirementById(jobId);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(response,"Requirement Fetched Successfully for "+jobId,HttpStatus.OK));
    }
    @GetMapping("/download-jd/{jobId}")
    public ResponseEntity<byte[]> downloadJD(
            @PathVariable String jobId){

     return requirementService.downloadJd(jobId);
    }
    @DeleteMapping("/delete-requirement/{jobId}")
    public ResponseEntity<ApiResponse<DeletedRequirementResponse>> deleteRequirement(
            @PathVariable String jobId,
            @RequestParam String userId){

       DeletedRequirementResponse response=requirementService.deleteRequirement(jobId,userId);
       return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(response,"Requirement Deleted For Job ID "+jobId,HttpStatus.OK));
    }
}
