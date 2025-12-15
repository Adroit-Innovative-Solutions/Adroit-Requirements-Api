package com.dataquadinc.utils;

import com.dataquadinc.model.RequirementInterview;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequirementInterviewSpecification {

    public static Specification<RequirementInterview> filterByCriteria(String keyword, Map<String, Object> filters) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate keywordPredicate = criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("candidateName")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("candidateEmail")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("clientName")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("interviewStatus")), likePattern),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("interviewLevel")), likePattern)
                );
                predicates.add(keywordPredicate);
            }

            if (filters != null && !filters.isEmpty()) {
                filters.forEach((key, value) -> {
                    if (value != null && !value.toString().trim().isEmpty()) {
                        switch (key) {
                            case "interviewStatus":
                                predicates.add(criteriaBuilder.equal(root.get("interviewStatus"), value));
                                break;
                            case "clientId":
                                predicates.add(criteriaBuilder.equal(root.get("clientId"), value));
                                break;
                            case "recruiterId":
                                predicates.add(criteriaBuilder.equal(root.get("recruiterId"), value));
                                break;
                            case "requirementId":
                                predicates.add(criteriaBuilder.equal(root.get("requirementId"), value));
                                break;
                            case "isPlaced":
                                predicates.add(criteriaBuilder.equal(root.get("isPlaced"), Boolean.parseBoolean(value.toString())));
                                break;
                        }
                    }
                });
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
