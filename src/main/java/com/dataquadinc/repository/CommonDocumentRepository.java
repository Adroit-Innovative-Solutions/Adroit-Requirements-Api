package com.dataquadinc.repository;

import com.dataquadinc.model.CommonDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonDocumentRepository extends JpaRepository<CommonDocument,Long> {
}
