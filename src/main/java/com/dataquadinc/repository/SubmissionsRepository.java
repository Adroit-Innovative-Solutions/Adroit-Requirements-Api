package com.dataquadinc.repository;

import com.dataquadinc.model.Submissions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface SubmissionsRepository extends JpaRepository<Submissions, String>, JpaSpecificationExecutor<Submissions> {

    Optional<Submissions> findTopByOrderByJobIdDesc();

    Submissions findByCandidateEmailAndJobId(String candidateEmail, String jobId);

    List<Submissions> findByRecruiterId(String recruiterId);
    List<Submissions> findByRecruiterIdIn(Set<String> recruiterIds);
    Submissions findByCandidateEmail(String candidateEmail);

    Optional<Submissions> findTopByOrderBySubmissionIdDesc();

    // Common search fields array for reuse
    String SEARCH_FIELDS =
            "LOWER(s.candidateName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.candidateEmail) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.mobileNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.visaType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.billRate) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.noticePeriod) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.currentLocation) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.qualification) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.employmentType) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.confirmRTR) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.overallFeedback) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.recruiterName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "LOWER(s.jobId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                    "CAST(s.totalExperience AS string) LIKE CONCAT('%', :keyword, '%') OR " +
                    "CAST(s.relevantExperience AS string) LIKE CONCAT('%', :keyword, '%')";

    // Unified search queries
    @Query("SELECT s FROM Submissions s WHERE s.recruiterId = :recruiterId AND " +
            "(:keyword IS NULL OR :keyword = '' OR " + SEARCH_FIELDS + ")")
    Page<Submissions> findByRecruiterId(@Param("recruiterId") String recruiterId,
                                                  @Param("keyword") String keyword,
                                                  Pageable pageable);

    @Query("SELECT s FROM Submissions s WHERE s.recruiterId IN :recruiterIds AND " +
            "(:keyword IS NULL OR :keyword = '' OR " + SEARCH_FIELDS + ")")
    Page<Submissions> findByRecruiterIdIn(@Param("recruiterIds") Set<String> recruiterIds,
                                                    @Param("keyword") String keyword,
                                                    Pageable pageable);

    @Query("SELECT s FROM Submissions s WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " + SEARCH_FIELDS + ")")
    Page<Submissions> findAll(@Param("keyword") String keyword, Pageable pageable);

    List<Submissions> findByJobId(String jobId);
}