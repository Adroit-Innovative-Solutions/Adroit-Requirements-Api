package com.dataquadinc.service.impl;

import com.dataquadinc.client.UserFeignClient;
import com.dataquadinc.dtos.*;
import com.dataquadinc.exceptions.ResourceNotFoundException;
import com.dataquadinc.filevalidators.FileValidator;
import com.dataquadinc.mapper.RequirementMapper;
import com.dataquadinc.model.JobRecruiter;
import com.dataquadinc.model.Requirement;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.repository.JobRecruiterRepository;
import com.dataquadinc.repository.RequirementRepository;
import com.dataquadinc.service.RequirementService;
import com.dataquadinc.service.RequirementServiceV2;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RequirementServiceImplV2 implements RequirementServiceV2 {

    @Autowired
    JobRecruiterRepositoryV2 jobRecruiterRepositoryV2;
    @Autowired
    RequirementRepositoryV2 requirementRepositoryV2;

    @Autowired
    CommonDocumentRepository commonDocumentRepository;

    @Override
    public void save(String userId, RequirementDTOV2 requirementDTO, MultipartFile jobDescriptionFile) {

    }
}
