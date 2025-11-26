package com.dataquadinc.controller;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.SubmissionDTO;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.service.CommonDocumentService;
import com.dataquadinc.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping(SystemConstants.API_BASE_PATH + "/requirements")
public class SubmissionController {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CommonDocumentService commonDocumentService;

    @PostMapping("/create-submission/{userId}")
    public ResponseEntity<SubmissionDTO> createSubmission(
            @PathVariable String userId,
            @RequestPart("dto") SubmissionDTO submissionDTO,
            @RequestPart(value = "resume", required = false) MultipartFile resume
    ) throws IOException {
        SubmissionDTO submission = submissionService.createSubmission(userId, submissionDTO, resume);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    @GetMapping("/get-submission/by-id/{submissionId}")
    public ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable String submissionId) {
        SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    @GetMapping("/get-submission/{userId}")
    public ResponseEntity<PageResponse<SubmissionDTO>> getSubmissionForAdmin(
            @PathVariable String userId,
            @RequestParam(defaultValue ="0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Map<String,Object> filters) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "updatedAt");

        Page<SubmissionDTO> submission = submissionService.getSubmission(userId, keyword, filters, pageable);
        PageResponse<SubmissionDTO> pageResponse = new PageResponse<>(submission);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

    @GetMapping("/get-submission/self/{userId}")
    public ResponseEntity<PageResponse<SubmissionDTO>> getSubmissionForTeamLead(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Map<String, Object> filters) {

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "updatedAt");

        Page<SubmissionDTO> submissionByTeamLead = submissionService.getSubmissionByTeamLead(userId, keyword, filters, pageable);
        PageResponse<SubmissionDTO> pageResponse = new PageResponse<>(submissionByTeamLead);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

    @GetMapping("/download-resume/{submissionId}")
    public ResponseEntity<ByteArrayResource> downloadResume(@PathVariable String submissionId) {
        CommonDocument commonDocument = commonDocumentService.findByFId(submissionId);

        if (commonDocument == null) {
            throw new ResourceNotFoundException("User Don`t have any submitted resumes ");
        }

        try {
            // Serve directly from DB
            ByteArrayResource resource = new ByteArrayResource(commonDocument.getData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(commonDocument.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + commonDocument.getFileName() + "\"")
                    .contentLength(commonDocument.getSize())
                    .body(resource);

        } catch (Exception e) {
            throw new ResourceNotFoundException("User Don`t have any submissions");
        }
    }

    @PutMapping(value = "update-submission/{submissionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SubmissionDTO> updateSubmission(
            @PathVariable String submissionId,
            @RequestPart("submissionDTO") SubmissionDTO submissionDTO,
            @RequestPart(value = "resume", required = false) MultipartFile resume
    ){
        SubmissionDTO updated = submissionService.updateSubmission(submissionId, submissionDTO, resume);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/delete-submission/{submissionId}")
    public ResponseEntity<String> deleteSubmission(@PathVariable String submissionId) {
        String s = submissionService.deleteSubmission(submissionId);
        return new ResponseEntity<>(s,HttpStatus.OK);
    }
}
