package com.dataquadinc.service;

import com.dataquadinc.model.CommonDocument;
import org.springframework.stereotype.Service;


public interface CommonDocumentService {
   CommonDocument findByFId(String submissionId);
}
