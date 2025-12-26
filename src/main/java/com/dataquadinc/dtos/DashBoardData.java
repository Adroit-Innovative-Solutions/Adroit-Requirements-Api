package com.dataquadinc.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashBoardData {
    private String totalConsultants;
    private String benchConsultants;

    private String currentMonthInterview;
    private String totalInterviews;

    private String currentMonthRequirements;
    private String currentMonthSubmissions;

//    private String currentMonthJoiningDateForConsultant;
//    private String currentMonthPlacements;
}
