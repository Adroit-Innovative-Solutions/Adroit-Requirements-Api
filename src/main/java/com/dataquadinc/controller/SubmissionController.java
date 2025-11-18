package com.dataquadinc.controller;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.*;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.service.CommonDocumentService;
import com.dataquadinc.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

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

    @GetMapping("/get-submission/{userId}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionForAdmin(@PathVariable String userId) {
        List<SubmissionDTO> submission = submissionService.getSubmission(userId);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    @GetMapping("/get-submission/self/{userId}")
    public ResponseEntity<List<SubmissionDTO>> getSubmissionForTeamLead(@PathVariable String userId) {
        List<SubmissionDTO> submissionByTeamLead = submissionService.getSubmissionByTeamLead(userId);
        return new ResponseEntity<>(submissionByTeamLead, HttpStatus.OK);
    }

    @GetMapping("/download-resume/{submissionId}")
    public ResponseEntity<ByteArrayResource> downloadResume(@PathVariable String submissionId) {
        CommonDocument commonDocument = commonDocumentService.findByFId(submissionId);

        if (commonDocument == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
