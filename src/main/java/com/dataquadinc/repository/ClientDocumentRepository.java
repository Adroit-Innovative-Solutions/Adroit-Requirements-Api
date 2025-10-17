package com.dataquadinc.repository;

import com.dataquadinc.model.ClientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDocumentRepository extends JpaRepository<ClientDocument, Long> {

    // Fetch all documents belonging to a specific client
    List<ClientDocument> findByClientClientId(String clientId);

    // Optional: fetch a specific document by clientId and file name
    ClientDocument findByClientClientIdAndFileName(String clientId, String fileName);
}
