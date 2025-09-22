package com.dataquadinc.utils;

import com.dataquadinc.model.Requirement;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.boot.autoconfigure.rsocket.RSocketProperties;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import jakarta.persistence.criteria.Predicate;

@Component
public class RequirementSpecifications {

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "jobId", "jobTitle", "clientName", "jobType", "location",
            "jobMode", "experienceRequired", "noticePeriod", "relevantExperience",
            "qualification", "salaryPackage", "noOfPositions", "visaType",
            "jobDescription", "status", "assignedById", "createdAt", "assignedByName","updatedAt",
            "createdBy", "updatedBy"
    );

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
                    "status", "assignedById","assignedByName"
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
                // keyword is not an integer, skip this filter
            }

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
    public static Specification<Requirement> createFiltersSpecification(Map<String, Object> filters){

        return ((root, query, criteriaBuilder) ->{
            if(filters.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates=new ArrayList<>();
               filters.forEach((field,value)->{
                    if(value!=null && ALLOWED_FIELDS.contains(field)){
                        switch (field){
                            case "jobId":
                            case "jobTitle":
                            case "clientName":
                            case "jobType":
                            case "location":
                            case "jobMode":
                            case "experienceRequired":
                            case "noticePeriod":
                            case "relevantExperience":
                            case "qualification":
                            case "salaryPackage":
                            case "visaType":
                            case "status":
                            case "assignedById":
                            case "assignedByName":
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)),value.toString()+"%"));
                                break;
                            case "noOfPositions":
                                try {
                                    Integer intValue = Integer.valueOf(value.toString());
                                    predicates.add(criteriaBuilder.equal(root.get(field), intValue));
                                }catch (NumberFormatException e){
                                    // If Value is Not Integer skip this filter
                                }
                                break;
                            case "createdAt":
                            case "updatedAt":
                            case "createdBy":
                            case "updatedBy":
                                if (value instanceof String && !value.toString().isBlank()) {
                                    try {
                                        LocalDateTime dateTime = LocalDateTime.parse(value.toString());
                                        predicates.add(criteriaBuilder.equal(root.get(field), dateTime));
                                    } catch (DateTimeParseException e) {
                                        // Optionally log or ignore invalid format
                                    }
                                }
                                break;
                        }
                    }
               });
               return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    public static Specification<Requirement> allRequirements(String keyword,Map<String,Object> filters){
        return Specification.<Requirement>where(isNotDeleted())
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }
    public static Specification<Requirement> requirementsAssignedByUser(
            String userId,String keyword,Map<String,Object> filters
    ){
        return Specification.where(isNotDeleted())
                .and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("assignedById"),userId)
                ))
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }

    public static Specification<Requirement> requirementsAssignedToUser(
            String userId,String keyword,Map<String,Object> filters
    ){
        return Specification.where(isNotDeleted())
                .and(((root, query, criteriaBuilder) ->{
                     // JOIN Requirement and JobRecruiters
                    Join<Object,Object> jrJoin=root.join("jobRecruiters", JoinType.INNER);
                    return criteriaBuilder.equal(jrJoin.get("userId"),userId);
                }))
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }

    public static Specification<Requirement> isNotDeleted() {
        return((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));
    }

}
