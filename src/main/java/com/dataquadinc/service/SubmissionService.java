package com.dataquadinc.service;


import com.dataquadinc.dtos.SubmissionDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

public interface SubmissionService {


    SubmissionDTO createSubmission(String userId, SubmissionDTO submissionDTO, MultipartFile resume) throws IOException;

    SubmissionDTO getSubmissionById(String submissionId);

    Page<SubmissionDTO> getSubmission(String userId, String keyword, Map<String, Object> filters, Pageable pageable);

    Page<SubmissionDTO> getSubmissionByTeamLead(String userId, String keyword, Map<String, Object> filters, Pageable pageable);

    SubmissionDTO updateSubmission(String submissionId, SubmissionDTO submissionDTO, MultipartFile resume);

    String deleteSubmission(String submissionId);
}
