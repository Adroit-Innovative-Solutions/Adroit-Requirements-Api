package com.dataquadinc.mapper;

import com.dataquadinc.dtos.SubmissionAddedResponse;
import com.dataquadinc.dtos.SubmissionDTO;
import com.dataquadinc.model.Submissions;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubmissionsMapper {

    Submissions toEntity(SubmissionDTO dto);

    SubmissionDTO toDTO(Submissions entity);

    SubmissionAddedResponse toSubmissionAddedResponse(Submissions entity);


}
