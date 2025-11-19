package com.dataquadinc.service;


import com.dataquadinc.dtos.SubmissionDTO;
import com.dataquadinc.model.Submissions;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SubmissionService {


    SubmissionDTO createSubmission(String userId, SubmissionDTO submissionDTO, MultipartFile resume) throws IOException;

    List<SubmissionDTO> getSubmission(String userId);

    List<SubmissionDTO> getSubmissionByTeamLead(String userId);

    SubmissionDTO updateSubmission(String submissionId, SubmissionDTO submissionDTO, MultipartFile resume);

    String deleteSubmission(String submissionId);
}
