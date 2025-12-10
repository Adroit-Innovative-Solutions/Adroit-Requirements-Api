package com.dataquadinc.repository;

import com.dataquadinc.model.SubmissionsMultiDocs;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface SubmissionsMultiDocsRepo extends JpaRepository<SubmissionsMultiDocs, Long> {
    List<SubmissionsMultiDocs> findBySubmissionId(String submissionId);
    SubmissionsMultiDocs findBySubmissionIdAndFileName( String submissionId,String filename);
    @Modifying
    @Transactional // you can place it here or on service
    @Query("DELETE FROM SubmissionsMultiDocs s WHERE s.submissionId = :submissionId AND s.fileName = :fileName")
    int deleteBySubmissionIdAndFileNameQuery(String submissionId,String fileName);

}
