package com.dataquadinc.mapper;

import com.dataquadinc.dtos.AddRequirementDTO;
import com.dataquadinc.dtos.DeletedRequirementResponse;
import com.dataquadinc.dtos.RequirementAddedResponse;
import com.dataquadinc.dtos.RequirementDTO;
import com.dataquadinc.model.Requirement;
import org.mapstruct.*;
import org.springframework.context.annotation.Bean;

@Mapper(componentModel = "spring")
public interface RequirementMapper {

    Requirement toEntity(RequirementDTO requirementDTO);

    RequirementDTO toDto(Requirement requirement);

    @Mapping(source = "visaType",target = "visaType")
    Requirement toEntity(AddRequirementDTO addRequirementDTO);

    RequirementAddedResponse toResponse(Requirement requirement);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
   void updateEntityFromDto(AddRequirementDTO dto, @MappingTarget Requirement requirement);

    DeletedRequirementResponse toDeletedRequirementResponse(Requirement requirement);

}
