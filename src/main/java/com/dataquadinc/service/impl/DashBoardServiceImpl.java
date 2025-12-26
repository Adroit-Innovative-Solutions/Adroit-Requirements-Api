package com.dataquadinc.service.impl;

import com.dataquadinc.dtos.DashBoardData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashBoardServiceImpl {

    private final JdbcTemplate jdbcTemplate;

    public DashBoardServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashBoardData getDashboardData() {
        String sql = """
            SELECT
              (SELECT COUNT(*)
               FROM consultant
               WHERE moved_to_hotlist = 1
                 AND status = 'ACTIVE'
                 AND is_deleted = 0) AS total_consultants,

              (SELECT COUNT(*)
               FROM consultant
               WHERE moved_to_hotlist = 0
                 AND status = 'ACTIVE'
                 AND is_deleted = 0) AS bench_consultants,

              (SELECT COUNT(*)
               FROM interviews_us
               WHERE is_deleted = 0) AS total_interviews,

              (SELECT COUNT(*)
               FROM interviews_us
               WHERE is_deleted = 0
                 AND MONTH(created_at) = MONTH(CURRENT_DATE)
                 AND YEAR(created_at) = YEAR(CURRENT_DATE)
              ) AS this_month_interviews,

              (SELECT COUNT(*)
               FROM submissions_us
               WHERE MONTH(created_at) = MONTH(CURRENT_DATE)
                 AND YEAR(created_at) = YEAR(CURRENT_DATE)
              ) AS this_month_submissions,

              (SELECT COUNT(*)
               FROM requirements_us_v2
               WHERE MONTH(created_at) = MONTH(CURRENT_DATE)
                 AND YEAR(created_at) = YEAR(CURRENT_DATE)
              ) AS this_month_requirements
        """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                DashBoardData.builder()
                        .totalConsultants(rs.getString("total_consultants"))
                        .benchConsultants(rs.getString("bench_consultants"))
                        .totalInterviews(rs.getString("total_interviews"))
                        .currentMonthInterview(rs.getString("this_month_interviews"))
                        .currentMonthSubmissions(rs.getString("this_month_submissions"))
                        .currentMonthRequirements(rs.getString("this_month_requirements"))
                        .build()
        );
    }
}
