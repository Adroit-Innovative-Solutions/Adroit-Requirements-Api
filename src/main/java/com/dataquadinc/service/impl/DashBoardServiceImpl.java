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
                (
                    SELECT COUNT(*)
                    FROM production.consultant
                    WHERE moved_to_hotlist = 1
                      AND status = 'ACTIVE'
                      AND is_deleted = 0
                      AND payroll <> 'FULL-TIME'
                ) AS totalHotlistExceptFullTime,

                (
                    SELECT COUNT(*)
                    FROM production.consultant
                    WHERE moved_to_hotlist = 1
                      AND status = 'ACTIVE'
                      AND is_deleted = 0
                      AND payroll = 'W2'
                ) AS w2HotlistCount,

                (
                    SELECT COUNT(*)
                    FROM production.rtr_us
                    WHERE is_deleted = 0
                      AND created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                      AND created_at < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
                ) AS rtrMonthlyCount,

                (
                    SELECT COUNT(*)
                    FROM production.interviews_us
                    WHERE is_deleted = 0
                      AND created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                      AND created_at < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
                ) AS currentMonthInterview,

                (
                    SELECT COUNT(*)
                    FROM production.requirements_us_v2
                    WHERE created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                      AND created_at < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
                ) AS currentMonthRequirements,

                (
                    SELECT COUNT(*)
                    FROM production.submissions_us
                    WHERE created_at >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
                      AND created_at < DATE_ADD(DATE_FORMAT(CURDATE(), '%Y-%m-01'), INTERVAL 1 MONTH)
                ) AS currentMonthSubmissions
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                DashBoardData.builder()
                        .totalHotlistExceptFullTime(rs.getString("totalHotlistExceptFullTime"))
                        .w2HotlistCount(rs.getString("w2HotlistCount"))
                        .rtrMonthlyCount(rs.getString("rtrMonthlyCount"))
                        .currentMonthInterview(rs.getString("currentMonthInterview"))
                        .currentMonthRequirements(rs.getString("currentMonthRequirements"))
                        .currentMonthSubmissions(rs.getString("currentMonthSubmissions"))
                        .build()
        );
    }
}
