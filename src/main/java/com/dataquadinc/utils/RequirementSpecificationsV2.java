package com.dataquadinc.utils;

import com.dataquadinc.model.JobRecruiterV2;
import com.dataquadinc.model.Requirement;
import com.dataquadinc.model.RequirementV2;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RequirementSpecificationsV2 {

    private static final Set<String> ALLOWED_FIELDS = Set.of(
            "jobId", "jobTitle", "clientName", "jobType", "location",
            "jobMode", "experienceRequired", "noticePeriod", "relevantExperience",
            "qualification", "salaryPackage", "noOfPositions", "visaType","clientId",
            "jobDescription", "status", "assignedById", "createdAt", "assignedByName","updatedAt",
            "createdBy", "updatedBy", "fromDate", "toDate"
    );

    public static Specification<RequirementV2> createSearchSpecification(String keyword) {

        return (root, query, criteriaBuilder) -> {

            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            String pattern = "%" + keyword.toLowerCase() + "%";

            List<Predicate> predicates = new ArrayList<>();

            List<String> stringFields = List.of(
                    "jobId", "jobTitle", "clientName", "jobType",
                    "location", "jobMode", "experienceRequired", "noticePeriod",
                    "relevantExperience", "qualification", "salaryPackage", "visaType",
                    "status", "assignedById","assignedByName","clientId"
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
    public static Specification<RequirementV2> createFiltersSpecification(Map<String, Object> filters){

        return ((root, query, criteriaBuilder) ->{
            if(filters.isEmpty()){
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates=new ArrayList<>();
            // ================== DATE RANGE LOGIC (ADD HERE) ==================
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            String fromDateStr = (String) filters.get("fromDate");
            String toDateStr   = (String) filters.get("toDate");

            try {
                if (fromDateStr != null && toDateStr != null) {
                    // RANGE
                    LocalDate fromDate = LocalDate.parse(fromDateStr, formatter);
                    LocalDate toDate = LocalDate.parse(toDateStr, formatter);

                    predicates.add(
                            criteriaBuilder.between(
                                    root.get("createdAt"),
                                    fromDate.atStartOfDay(),
                                    toDate.atTime(23, 59, 59)
                            )
                    );
                } else if (fromDateStr != null) {
                    // SINGLE DAY (fromDate)
                    LocalDate date = LocalDate.parse(fromDateStr, formatter);

                    predicates.add(
                            criteriaBuilder.between(
                                    root.get("createdAt"),
                                    date.atStartOfDay(),
                                    date.atTime(23, 59, 59)
                            )
                    );
                } else if (toDateStr != null) {
                    // SINGLE DAY (toDate)
                    LocalDate date = LocalDate.parse(toDateStr, formatter);

                    predicates.add(
                            criteriaBuilder.between(
                                    root.get("createdAt"),
                                    date.atStartOfDay(),
                                    date.atTime(23, 59, 59)
                            )
                    );
                }
            } catch (DateTimeParseException e) {
                // Optional: log invalid date format
            }
            // ================== END DATE LOGIC ==================

            // ================== OTHER FILTERS ==================
               filters.forEach((field,value)->{
                    if(value!=null && ALLOWED_FIELDS.contains(field)
                    && !field.equals("fromDate")
                           && !field.equals("toDate")){
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
                            case "clientId":
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get(field)),"%" + value.toString().toLowerCase() + "%"));
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

    public static Specification<RequirementV2> allRequirements(String keyword,Map<String,Object> filters){
        return Specification.where(isNotDeleted())
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }
    public static Specification<RequirementV2> requirementsAssignedByUser(
            String userId,String keyword,Map<String,Object> filters
    ){
        return Specification.where(isNotDeleted())
                .and(((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("assignedById"),userId)
                ))
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }

    public static Specification<RequirementV2> requirementsAssignedToUser(
            String userId,String keyword,Map<String,Object> filters
    ){
        return Specification.where(isNotDeleted())
                .and(((root, query, criteriaBuilder) ->{
                    // Use subquery to find requirements assigned to user
                    var subquery = query.subquery(String.class);
                    var jobRecruiterRoot = subquery.from(JobRecruiterV2.class);
                    subquery.select(jobRecruiterRoot.get("requirementId"))
                           .where(criteriaBuilder.equal(jobRecruiterRoot.get("userId"), userId));

                    // Combine subquery with direct assignedById check
                    Predicate assignedToUser = criteriaBuilder.in(root.get("jobId")).value(subquery);
                    Predicate assignedByUser = criteriaBuilder.equal(root.get("assignedById"), userId);

                    return criteriaBuilder.or(assignedToUser, assignedByUser);
                }))
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }

    public static Specification<RequirementV2> requirementsForTeamLead(
            String userId, String keyword, Map<String, Object> filters
    ) {
        return Specification.where(isNotDeleted())
                .and(((root, query, criteriaBuilder) -> {
                    // Check if userId is present in teamsLeadIds JSON field OR assignedById
                    Predicate inTeamLeadIds = criteriaBuilder.like(root.get("teamsLeadIds"), "%" + userId + "%");
                    Predicate assignedByUser = criteriaBuilder.equal(root.get("assignedById"), userId);
                    return criteriaBuilder.or(inTeamLeadIds, assignedByUser);
                }))
                .and(createSearchSpecification(keyword))
                .and(createFiltersSpecification(filters));
    }

    public static Specification<RequirementV2> isNotDeleted() {
        return((root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isDeleted")));
    }

}
