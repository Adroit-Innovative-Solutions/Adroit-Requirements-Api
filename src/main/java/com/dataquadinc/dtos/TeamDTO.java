package com.dataquadinc.dtos;

import lombok.Data;
import java.util.List;

@Data
public class TeamDTO {

    private String teamName;
    private String teamLeadId;
    private String teamLeadName;
    private List<UserMiniDTO> employees;
}

