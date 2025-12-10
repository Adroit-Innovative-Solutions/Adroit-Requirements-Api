package com.dataquadinc.service.impl;

import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.mapper.SubmissionsMapper;
import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.model.Submissions;
import com.dataquadinc.model.SubmissionsMultiDocs;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.repository.SubmissionsMultiDocsRepo;
import com.dataquadinc.repository.SubmissionsRepository;
import com.dataquadinc.service.SubmissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SubmissionServiceImpl implements SubmissionService {

    @Autowired
    SubmissionsRepository submissionsRepository;
    @Autowired
    SubmissionsMapper submissionsMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CommonDocumentRepository commonDocumentRepository;

    @Autowired
    SubmissionsMultiDocsRepo submissionsMultiDocsRepo;


    private final String teamUrl = "https://mymulya.com/users/associated-users/";
    private final String userUrl = "https://mymulya.com/users/user/";

    @Override
    public SubmissionDTO createSubmission(String userId, SubmissionDTO submissionDTO, MultipartFile resume, List<MultipartFile> documents) throws IOException {

        findIsDuplicateSubmission(submissionDTO);
        Submissions submission = new Submissions();

        Submissions entity = submissionsMapper.toEntity(submissionDTO);
        entity.setCreatedBy(userId);
        entity.setSubmissionId(generateSubmissionId());
        entity.setRecruiterId(userId);

        Submissions savedSubmission = submissionsRepository.save(entity);

        if (savedSubmission != null) {
            CommonDocument commonDocument = new CommonDocument();
            commonDocument.setCommonDocId(savedSubmission.getSubmissionId());
            commonDocument.setFileName(resume.getOriginalFilename());
            commonDocument.setSize(resume.getSize());
            commonDocument.setData(resume.getBytes());
            commonDocument.setContentType(resume.getContentType());
            commonDocument.setUploadedAt(LocalDateTime.now());
            CommonDocument save = commonDocumentRepository.save(commonDocument);
        }
        if (documents != null && !documents.isEmpty()) {
            List<SubmissionsMultiDocs> submissionsMultiDocs = documents.stream()
                    .filter(doc -> doc != null && !doc.isEmpty())
                    .map(doc -> {
                        SubmissionsMultiDocs md = new SubmissionsMultiDocs();
                        md.setSubmissionId(savedSubmission.getSubmissionId());
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
        }

        SubmissionDTO dto = submissionsMapper.toDTO(savedSubmission);
        return dto;
    }

    @Override
    public SubmissionDTO getSubmissionById(String submissionId) {
        log.info("Fetching submission by ID: {}", submissionId);

        Optional<Submissions> submissionOptional = submissionsRepository.findById(submissionId);
        List<SubmissionsMultiDocs> bySubmissionId = submissionsMultiDocsRepo.findBySubmissionId(submissionId);

        Set<String> collect=new HashSet<>();
        if (!bySubmissionId.isEmpty() && bySubmissionId != null) {
            collect = bySubmissionId.stream().map(a -> a.getFileName()).collect(Collectors.toSet());
        }

        if (submissionOptional.isEmpty()) {
            throw new ResourceNotFoundException("Submission not found with id: " + submissionId);
        }

        SubmissionDTO dto = submissionsMapper.toDTO(submissionOptional.get());
        dto.setFileName(collect);
        return dto;
    }

    @Override
    public Page<SubmissionDTO> getSubmission(String userId, String keyword, Map<String, Object> filters, Pageable pageable)
    {

        String teamUrlL = teamUrl + userId;
        String userUrlL = userUrl + userId;

        try {

            System.out.println("Calling Team API: " + teamUrlL);
            System.out.println("Calling User API: " + userUrlL);

            ResponseEntity<TeamDTO> teamResponse = restTemplate.getForEntity(teamUrlL, TeamDTO.class);
            ResponseEntity<ApiResponse> userResponse = restTemplate.getForEntity(userUrlL, ApiResponse.class);
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            UserDTO userDTO = mapper.convertValue(userResponse.getBody().getData(), UserDTO.class);

            if (userDTO.getRoles().contains("SUPERADMIN")) {
                Page<SubmissionDTO> page = submissionsRepository.findAll(keyword, pageable)
                        .map(submissionsMapper::toDTO);
                if (page.getContent().isEmpty()) {
                    throw new ResourceNotFoundException("User Don`t have any submissions");
                }
                return page;
            }
            if (userDTO.getRoles().contains("TEAMLEAD")) {
                Set<String> collect = teamResponse.getBody().getRecruiters().stream()
                        .map(r -> r.getUserId())
                        .collect(Collectors.toSet());

                collect.add(userId);

                Page<SubmissionDTO> page = submissionsRepository.findByRecruiterIdIn(collect, keyword, pageable)
                        .map(submissionsMapper::toDTO);
                if (page.getContent().isEmpty()) {
                    throw new ResourceNotFoundException("User Don`t have any submissions");
                }
                return page;
            }
            if (userDTO.getRoles().contains("RECRUITER")) {
                Page<SubmissionDTO> page = submissionsRepository.findByRecruiterId(userId, keyword, pageable)
                        .map(submissionsMapper::toDTO);
                if (page.getContent().isEmpty()) {
                    throw new ResourceNotFoundException("User Don`t have any submissions");
                }
                return page;
            }
            throw new ResourceNotFoundException("User Don`t have any submissions");

        } catch (Exception e) {
            System.out.println("Exception details: " + e.getMessage());
            e.printStackTrace();
            throw new ResourceNotFoundException("Exception occurs while calling external api`s.");
        }
    }

    @Override
    public Page<SubmissionDTO> getSubmissionByTeamLead(String userId, String keyword, Map<String,Object> filters, Pageable pageable) {
        Page<SubmissionDTO> page = submissionsRepository.findByRecruiterId(userId, keyword, pageable)
                .map(submissionsMapper::toDTO);
        if (page.getContent().isEmpty()) {
            throw new ResourceNotFoundException("No Data Found");
        } else {
            return page;
        }
    }

    public String generateSubmissionId() {
        String lastSubmissionId = submissionsRepository.findTopByOrderBySubmissionIdDesc()
                .map(Submissions::getSubmissionId)
                .orElse("SUB000000");

        int num = Integer.parseInt(lastSubmissionId.replace("SUB", "")) + 1;
        return String.format("SUB%06d", num);
    }


    public void findIsDuplicateSubmission(SubmissionDTO submissionDTO) {

        Submissions submissions = submissionsRepository.findByCandidateEmail(submissionDTO.getCandidateEmail());
        if (submissions != null && submissions.getCandidateEmail() != null) {
            throw new ResourceNotFoundException("Candidate Already Submitted For Job ID " + submissions.getJobId() + " Submitted By " + submissions.getRecruiterId());
        }
    }

    @Override
    public SubmissionDTO updateSubmission(String submissionId, SubmissionDTO submissionDTO, MultipartFile resume) {

        Submissions existing = submissionsRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found"));

        existing.setCandidateName(submissionDTO.getCandidateName());
        existing.setCandidateEmail(submissionDTO.getCandidateEmail());
        existing.setDob(submissionDTO.getDob());
        existing.setMobileNumber(submissionDTO.getMobileNumber());
        existing.setRecruiterId(submissionDTO.getRecruiterId());
        existing.setRecruiterName(submissionDTO.getRecruiterName());
        existing.setJobId(submissionDTO.getJobId());
        existing.setVisaType(submissionDTO.getVisaType());
        existing.setBillRate(submissionDTO.getBillRate());
        existing.setConfirmRTR(submissionDTO.getConfirmRTR());
        existing.setNoticePeriod(submissionDTO.getNoticePeriod());
        existing.setCurrentLocation(submissionDTO.getCurrentLocation());
        existing.setTotalExperience(submissionDTO.getTotalExperience());
        existing.setRelevantExperience(submissionDTO.getRelevantExperience());
        existing.setQualification(submissionDTO.getQualification());
        existing.setOverallFeedback(submissionDTO.getOverallFeedback());
        existing.setRelocation(submissionDTO.isRelocation());
        existing.setEmploymentType(submissionDTO.getEmploymentType());

        Submissions updated = submissionsRepository.save(existing);

        // Resume updation
        if (resume != null && !resume.isEmpty()) {
            CommonDocument commonDocument = commonDocumentRepository.findByCommonDocId(submissionId);

            if (commonDocument == null) {
                commonDocument = new CommonDocument();
                commonDocument.setCommonDocId(submissionId);
            }
            commonDocument.setFileName(resume.getOriginalFilename());
            commonDocument.setContentType(resume.getContentType());
            commonDocument.setSize(resume.getSize());
            commonDocument.setUploadedAt(LocalDateTime.now());
            try {
                commonDocument.setData(resume.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            commonDocumentRepository.save(commonDocument);
        }

        return submissionsMapper.toDTO(updated);
    }

    @Override
    public String deleteSubmission(String submissionId) {
        Optional<Submissions> submissions = submissionsRepository.findById(submissionId);

        if (submissions.isEmpty()) {
            throw new ResourceNotFoundException("Submission not found");
        }
        submissionsRepository.delete(submissions.get());
        CommonDocument byCommonDocId = commonDocumentRepository.findByCommonDocId(submissionId);
        if (byCommonDocId == null) {
            throw new ResourceNotFoundException("Resume not found");
        }
        commonDocumentRepository.delete(byCommonDocId);

        return "Data Deleted";
    }


}
