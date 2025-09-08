package com.dataquadinc.utils;

import com.dataquadinc.model.Requirement;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.Predicate;

@Component
public class RequirementSpecifications {

    public static Specification<Requirement> createSearchSpecification(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + keyword + "%";

            List<Predicate> predicates = new ArrayList<>();

            List<String> stringFields = List.of(
                    "jobId", "jobTitle", "clientName", "jobType",
                    "location", "jobMode", "experienceRequired", "noticePeriod",
                    "relevantExperience", "qualification", "salaryPackage", "visaType",
                    "status", "assignedBy"
            );
            // Building Fields Dynamically
            for (String field : stringFields) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)), pattern));
            }
            // Integer Fields
            try {
                Integer intValue = Integer.valueOf(keyword);
                predicates.add(criteriaBuilder.equal(root.get("noOfPositions"), intValue));
            }catch (NumberFormatException ignored){
                // // keyword is not an integer, skip this filter
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Requirement> allRequirementsSearch(String keyword){
        return Specification.<Requirement>where(isNotDeleted())
                .and(createSearchSpecification(keyword));
    }

    public static Specification<Requirement> isNotDeleted() {
        return((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));
    }

}
