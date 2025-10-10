package com.dataquadinc.repository;



import com.dataquadinc.model.Client;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client,String> {

    @Query("SELECT c.clientName FROM Client c WHERE c.clientName = :clientName")
    List<String> findByClientName(String clientName);

    @Query("SELECT b FROM Client b")
    List<Client> getClients();

    @Query(value = "SELECT COUNT(*) FROM requirements_us WHERE client_name = :clientName", nativeQuery = true)
    int countRequirementsByClientName(@Param("clientName") String clientName);

}
