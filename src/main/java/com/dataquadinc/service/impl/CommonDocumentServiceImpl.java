package com.dataquadinc.service.impl;

import com.dataquadinc.model.CommonDocument;
import com.dataquadinc.service.CommonDocumentService;
import org.springframework.beans.factory.annotation.Autowired;


public class CommonDocumentServiceImpl implements CommonDocumentService  {

    @Autowired
    CommonDocumentService commonDocumentService;
    @Override
    public CommonDocument findByFId(String submissionId) {
        CommonDocument commonDocument = commonDocumentService.findByFId(submissionId);
        return commonDocument;
    }
}
