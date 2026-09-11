package com.dataquadinc.utils;

import com.dataquadinc.model.Submissions;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubmissionSpecification {

    public static Specification<Submissions> filter(
            String keyword,
            Map<String, Object> filters) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {

                String search =
                        "%" + keyword.trim().toLowerCase() + "%";

                List<Predicate> searchPredicates = new ArrayList<>();

                addLike(searchPredicates, cb, root, "submissionId", search);
                addLike(searchPredicates, cb, root, "candidateName", search);
                addLike(searchPredicates, cb, root, "candidateEmail", search);
                addLike(searchPredicates, cb, root, "mobileNumber", search);
                addLike(searchPredicates, cb, root, "recruiterId", search);
                addLike(searchPredicates, cb, root, "recruiterName", search);
                addLike(searchPredicates, cb, root, "jobId", search);
                addLike(searchPredicates, cb, root, "visaType", search);
                addLike(searchPredicates, cb, root, "billRate", search);
                addLike(searchPredicates, cb, root, "payRate", search);
                addLike(searchPredicates, cb, root, "confirmRTR", search);
                addLike(searchPredicates, cb, root, "noticePeriod", search);
                addLike(searchPredicates, cb, root, "currentLocation", search);
                addLike(searchPredicates, cb, root, "qualification", search);
                addLike(searchPredicates, cb, root, "employmentType", search);
                addLike(searchPredicates, cb, root, "overallFeedback", search);

                try {
                    Double number = Double.parseDouble(keyword.trim());

                    searchPredicates.add(cb.equal(root.get("totalExperience"), number));
                    searchPredicates.add(cb.equal(root.get("relevantExperience"), number));

                } catch (NumberFormatException ignored) {
                }

                if ("true".equalsIgnoreCase(keyword.trim())
                        || "false".equalsIgnoreCase(keyword.trim())) {

                    searchPredicates.add(cb.equal(root.get("relocation"), Boolean.parseBoolean(keyword.trim())));
                }

                predicates.add(cb.or(searchPredicates.toArray(new Predicate[0])));
            }

            if (filters != null) {

                Object fromDateValue = filters.get("fromDate");

                if (fromDateValue != null && !fromDateValue.toString().isBlank()) {

                    LocalDate date = parseDate(fromDateValue.toString());

                    if (date != null) {

                        LocalDateTime fromDate = date.atStartOfDay();
                        predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate));
                    }
                }
                Object toDateValue = filters.get("toDate");

                if (toDateValue != null && !toDateValue.toString().isBlank()) {

                    LocalDate date = parseDate(toDateValue.toString());

                    if (date != null) {

                        LocalDateTime toDate = date.atTime(LocalTime.MAX);
                        predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate));
                    }
                }

                addStringFilter(filters, "submissionId", "submissionId", root, cb, predicates);
                addStringFilter(filters, "candidateName", "candidateName", root, cb, predicates);
                addStringFilter(filters, "email", "candidateEmail", root, cb, predicates);
                addStringFilter(filters, "mobile", "mobileNumber", root, cb, predicates);
                addStringFilter(filters, "jobId", "jobId", root, cb, predicates);

                if (hasValue(filters, "recruiter")) {
                    String value = filters.get("recruiter").toString().trim().toLowerCase();
                    String search = "%" + value + "%";
                    predicates.add(cb.or(cb.like(cb.lower(root.get("recruiterName")), search), cb.like(cb.lower(root.get("recruiterId")), search)));
                }

                addStringFilter(filters, "visaType", "visaType", root, cb, predicates);
                addStringFilter(filters, "billRate", "billRate", root, cb, predicates);
                addStringFilter(filters, "payRate", "payRate", root, cb, predicates);
                addStringFilter(filters, "confirmRTR", "confirmRTR", root, cb, predicates);
                addStringFilter(filters, "noticePeriod", "noticePeriod", root, cb, predicates);
                addStringFilter(filters, "location", "currentLocation", root, cb, predicates);
                addStringFilter(filters, "qualification", "qualification", root, cb, predicates);
                addStringFilter(filters, "employmentType", "employmentType", root, cb, predicates);
                addDoubleFilter(filters, "totalExperience", root, cb, predicates);
                addDoubleFilter(filters, "relevantExperience", root, cb, predicates);

                if (hasValue(filters, "relocation")) {

                    String value = filters.get("relocation").toString().trim();

                    if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                        predicates.add(cb.equal(root.get("relocation"), Boolean.parseBoolean(value)));
                    }

                    if ("yes".equalsIgnoreCase(value) || "no".equalsIgnoreCase(value)) {

                        predicates.add(cb.equal(root.get("relocation"), "yes".equalsIgnoreCase(value)));
                    }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    private static void addLike(
            List<Predicate> predicates,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Root<Submissions> root,
            String field,
            String search) {

        predicates.add(cb.like(cb.lower(root.get(field)), search));
    }


    private static void addStringFilter(
            Map<String, Object> filters,
            String filterKey,
            String entityField,
            jakarta.persistence.criteria.Root<Submissions> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {

        if (!hasValue(filters, filterKey)) {
            return;
        }

        String value = filters.get(filterKey).toString().trim().toLowerCase();

        predicates.add(cb.like(cb.lower(root.get(entityField)), "%" + value + "%"));
    }


    private static void addDoubleFilter(
            Map<String, Object> filters,
            String filterKey,
            jakarta.persistence.criteria.Root<Submissions> root,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            List<Predicate> predicates) {

        if (!hasValue(filters, filterKey)) {
            return;
        }

        try {
            Double value = Double.parseDouble(filters.get(filterKey).toString().trim());

            predicates.add(cb.equal(root.get(filterKey), value));

        } catch (NumberFormatException ignored) {
        }
    }

    private static boolean hasValue(
            Map<String, Object> filters,
            String key) {

        if (filters == null || !filters.containsKey(key)) {
            return false;
        }

        Object value = filters.get(key);

        return value != null && !value.toString().trim().isEmpty();
    }

    private static LocalDate parseDate(String value) {

        try {
            return LocalDate.parse(value.trim());

        } catch (Exception ignored) {
        }

        try {
            return LocalDate.parse(
                    value.trim(),
                    java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        } catch (Exception ignored) {
        }

        return null;
    }
}
