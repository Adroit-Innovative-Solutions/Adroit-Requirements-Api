package com.dataquadinc.service;


import com.dataquadinc.dtos.SubmissionAddedResponse;
import com.dataquadinc.dtos.SubmissionDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SubmissionService {


    SubmissionAddedResponse createSubmission(String userId, SubmissionDTO submissionDTO, MultipartFile resume) throws IOException;

}
