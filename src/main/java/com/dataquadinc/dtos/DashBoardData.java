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

    private String totalHotlistExceptFullTime;
    private String w2HotlistCount;
    private String rtrMonthlyCount;
    private String currentMonthInterview;

    private String currentMonthRequirements;
    private String currentMonthSubmissions;
    private String totalPlacementsCurrentMonth;
    private String totalPlacementsOverall;
}
