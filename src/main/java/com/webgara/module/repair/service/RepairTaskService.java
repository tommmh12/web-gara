package com.webgara.module.repair.service;

import com.webgara.module.repair.dto.RepairTaskRequest;
import com.webgara.module.repair.dto.RepairTaskResponse;
import com.webgara.module.repair.model.RepairTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RepairTaskService {
    RepairTaskResponse createTask(RepairTaskRequest request);
    RepairTaskResponse getById(String id);
    List<RepairTaskResponse> listTasksByRepairOrder(String repairOrderId);
    RepairTaskResponse updateTaskStatus(String taskId, RepairTaskStatus newStatus, String technicianId);
    RepairTaskResponse updateTaskProgress(String taskId, Integer actualMinutes, String notes);
    Page<RepairTaskResponse> listTasksByTechnicianAndStatus(String technicianId, RepairTaskStatus status, Pageable pageable);
}
