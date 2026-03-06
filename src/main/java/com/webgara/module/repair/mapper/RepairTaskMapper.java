package com.webgara.module.repair.mapper;

import com.webgara.module.repair.dto.RepairTaskRequest;
import com.webgara.module.repair.dto.RepairTaskResponse;
import com.webgara.module.repair.model.RepairTask;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepairTaskMapper {
    
    RepairTask toEntity(RepairTaskRequest request);
    
    RepairTaskResponse toResponse(RepairTask repairTask);
    
    List<RepairTaskResponse> toResponseList(List<RepairTask> repairTasks);
}
