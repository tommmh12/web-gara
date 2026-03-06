package com.webgara.module.repair.mapper;

import com.webgara.module.repair.dto.RepairTaskRequest;
import com.webgara.module.repair.dto.RepairTaskResponse;
import com.webgara.module.repair.model.RepairTask;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RepairTaskMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    RepairTask toEntity(RepairTaskRequest request);
    
    RepairTaskResponse toResponse(RepairTask repairTask);
    
    List<RepairTaskResponse> toResponseList(List<RepairTask> repairTasks);
}
