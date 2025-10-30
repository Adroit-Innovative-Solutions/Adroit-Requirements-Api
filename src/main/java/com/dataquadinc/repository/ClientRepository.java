package com.dataquadinc.repository;

import com.dataquadinc.model.Client;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

    // Check if a client with the same name exists
    @Query("SELECT c.clientName FROM Client c WHERE c.clientName = :clientName")
    List<String> findByClientName(@Param("clientName") String clientName);

    // Fetch all clients
    @Query("SELECT b FROM Client b")
    List<Client> getClients();

    // Fetch all clients along with their documents (EntityGraph ensures documents are eagerly loaded)
    @EntityGraph(attributePaths = {"documents"})
    @Query("SELECT c FROM Client c")
    List<Client> findAllWithDocuments();

    // Count number of requirements for a given client
    @Query(value = "SELECT COUNT(*) FROM requirements_us WHERE client_name = :clientName", nativeQuery = true)
    int countRequirementsByClientName(@Param("clientName") String clientName);
}
