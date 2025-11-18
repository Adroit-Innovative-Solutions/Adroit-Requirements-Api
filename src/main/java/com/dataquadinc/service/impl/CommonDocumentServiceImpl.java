package com.dataquadinc.service.impl;

import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.repository.CommonDocumentRepository;
import com.dataquadinc.service.CommonDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonDocumentServiceImpl implements CommonDocumentService  {

    @Autowired
    CommonDocumentRepository commonDocumentRepository;

    @Override
    public CommonDocument findByFId(String submissionId) {
        CommonDocument commonDocument = commonDocumentRepository.findByFId(submissionId);
        return commonDocument;
    }
}
