package com.dataquadinc.controller;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.commons.PageResponse;
import com.dataquadinc.commons.SystemConstants;
import com.dataquadinc.dtos.SubmissionDTO;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.model.Submissions;
import com.dataquadinc.model.SubmissionsMultiDocs;
import com.dataquadinc.repository.SubmissionsMultiDocsRepo;
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
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping(SystemConstants.API_BASE_PATH + "/requirements")
public class SubmissionController {

    @Autowired
    SubmissionService submissionService;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CommonDocumentService commonDocumentService;

    @Autowired
    SubmissionsMultiDocsRepo submissionsMultiDocsRepo;

    @PostMapping("/create-submission/{userId}")
    public ResponseEntity<SubmissionDTO> createSubmission(
            @PathVariable String userId,
            @RequestPart("dto") SubmissionDTO submissionDTO,
            @RequestPart(value = "resume", required = false) MultipartFile resume,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents
    ) throws IOException {
        SubmissionDTO submission = submissionService.createSubmission(userId, submissionDTO, resume,documents);
        return new ResponseEntity<>(submission, HttpStatus.CREATED);
    }

    @GetMapping("/get-submission/by-id/{submissionId}")
    public ResponseEntity<SubmissionDTO> getSubmissionById(@PathVariable String submissionId) {
        SubmissionDTO submission = submissionService.getSubmissionById(submissionId);
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }
    @GetMapping("/get-submission/by-jobid/{jobId}")
    public ResponseEntity<List<Submissions>> getSubmissionByIdJobId(@PathVariable String jobId) {
        List<Submissions> submission = submissionService.getSubmissionsByJobId(jobId);
        if(submission.isEmpty()||submission==null){
            throw new ResourceNotFoundException("No Submissions Found For This Job Id");
        }
        return new ResponseEntity<>(submission, HttpStatus.OK);
    }

    @GetMapping("/get-submission/{userId}")
    public ResponseEntity<PageResponse<SubmissionDTO>> getSubmissionForAdmin(
            @PathVariable String userId,
            @RequestParam(defaultValue ="0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Map<String,Object> filters) {

        // Initialize filters map if null
        if (filters == null) {
            filters = new HashMap<>();
        }

        // Add date filters
        if (fromDate != null && !fromDate.isEmpty()) {
            filters.put("fromDate", fromDate);
        }
        if (toDate != null && !toDate.isEmpty()) {
            filters.put("toDate", toDate);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

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

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");

        Page<SubmissionDTO> submissionByTeamLead = submissionService.getSubmissionByTeamLead(userId, keyword, filters, pageable);
        PageResponse<SubmissionDTO> pageResponse = new PageResponse<>(submissionByTeamLead);
        return new ResponseEntity<>(pageResponse, HttpStatus.OK);
    }

    @GetMapping("/download-multidoc/{submissionId}/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadMultiDoc(@PathVariable String submissionId,@PathVariable String fileName) {
        SubmissionsMultiDocs bySubmissionIdAndFileName = submissionsMultiDocsRepo.findBySubmissionIdAndFileName(submissionId, fileName);

        if (bySubmissionIdAndFileName == null) {
            throw new ResourceNotFoundException("User Don`t have any submitted resumes ");
        }

        try {
            // Serve directly from DB
            ByteArrayResource resource = new ByteArrayResource(bySubmissionIdAndFileName.getData());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(bySubmissionIdAndFileName.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + bySubmissionIdAndFileName.getFileName() + "\"")
                    .contentLength(bySubmissionIdAndFileName.getSize())
                    .body(resource);

        } catch (Exception e) {
            throw new ResourceNotFoundException("User Don`t have any submissions");
        }

    }

    @PostMapping("/save-multi-doc/{submissionId}")
    public ResponseEntity<String> saveMultidocument(
            @PathVariable String submissionId,
            @RequestParam("file") List<MultipartFile> file) throws IOException {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded.");
        }
        List<SubmissionsMultiDocs> submissionsMultiDocs = file.stream()
                .filter(doc -> doc != null && !doc.isEmpty())
                .map(doc -> {
                    SubmissionsMultiDocs md = new SubmissionsMultiDocs();
                    md.setSubmissionId(submissionId);
                    md.setFileName(doc.getOriginalFilename());
                    md.setSize(doc.getSize());
                    try {
                        md.setData(doc.getBytes());
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                    md.setContentType(doc.getContentType());
                    md.setUploadedAt(LocalDateTime.now());
                    return md;
                })
                .collect(Collectors.toList());
        submissionsMultiDocsRepo.saveAll(submissionsMultiDocs);
        return ResponseEntity.ok("Files saved successfully");
    }

    @DeleteMapping("/delete-multi-doc/{submissionId}/{fileName:.+}")
    public ResponseEntity deleteMultiDoc(
            @PathVariable String submissionId,
            @PathVariable String fileName) {

        submissionsMultiDocsRepo.deleteBySubmissionIdAndFileNameQuery(submissionId, fileName);
        return ResponseEntity.ok("File deleted successfully");
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
