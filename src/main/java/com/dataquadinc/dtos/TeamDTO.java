package com.dataquadinc.dtos;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TeamDTO {

    private String teamName;
    private String teamLeadId;
    private String teamLeadName;

    private List<AssociatedUser> salesExecutives = new ArrayList<>();
    private List<AssociatedUser> recruiters = new ArrayList<>();
    private List<AssociatedUser> employees = new ArrayList<>();
    private List<AssociatedUser> coordinators = new ArrayList<>();
    private List<AssociatedUser> bdms = new ArrayList<>();
    private List<AssociatedUser> teamLeads = new ArrayList<>();
}


