package com.dataquadinc.repository;

import com.dataquadinc.model.CommonDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonDocumentRepository extends JpaRepository<CommonDocument,Long> {
    CommonDocument findByCommonDocId(String submissionId);
}