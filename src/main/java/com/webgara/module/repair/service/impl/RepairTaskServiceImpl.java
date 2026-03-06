package com.webgara.module.repair.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.repair.dto.RepairTaskRequest;
import com.webgara.module.repair.dto.RepairTaskResponse;
import com.webgara.module.repair.mapper.RepairTaskMapper;
import com.webgara.module.repair.model.RepairTask;
import com.webgara.module.repair.model.RepairTaskPriority;
import com.webgara.module.repair.model.RepairTaskStatus;
import com.webgara.module.repair.repository.RepairTaskRepository;
import com.webgara.module.repair.service.RepairTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairTaskServiceImpl implements RepairTaskService {

    private final RepairTaskRepository repairTaskRepository;
    private final RepairTaskMapper repairTaskMapper;

    @Override
    @Transactional
    public RepairTaskResponse createTask(RepairTaskRequest request) {
        RepairTask task = RepairTask.builder()
                .repairOrderId(request.getRepairOrderId())
                .technicianId(request.getTechnicianId())
                .description(request.getDescription())
                .serviceId(request.getServiceId())
                .partsUsed(new ArrayList<>())
                .status(RepairTaskStatus.ASSIGNED)
                .priority(RepairTaskPriority.valueOf(request.getPriority()))
                .estimatedMinutes(request.getEstimatedMinutes())
                .build();

        RepairTask saved = repairTaskRepository.save(task);
        return repairTaskMapper.toResponse(saved);
    }

    @Override
    public RepairTaskResponse getById(String id) {
        RepairTask task = repairTaskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairTask", "id", id));
        return repairTaskMapper.toResponse(task);
    }

    @Override
    public List<RepairTaskResponse> listTasksByRepairOrder(String repairOrderId) {
        List<RepairTask> tasks = repairTaskRepository.findByRepairOrderId(repairOrderId);
        return repairTaskMapper.toResponseList(tasks);
    }

    @Override
    @Transactional
    public RepairTaskResponse updateTaskStatus(String taskId, RepairTaskStatus newStatus, String technicianId) {
        RepairTask task = repairTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("RepairTask", "id", taskId));

        if (!task.getTechnicianId().equals(technicianId)) {
            throw new BadRequestException("You can only update your own tasks");
        }

        RepairTaskStatus currentStatus = task.getStatus();

        // Validate transition
        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        LocalDateTime startedAt = task.getStartedAt();
        LocalDateTime completedAt = task.getCompletedAt();

        if (newStatus == RepairTaskStatus.IN_PROGRESS) {
            startedAt = LocalDateTime.now();
        } else if (newStatus == RepairTaskStatus.COMPLETED) {
            completedAt = LocalDateTime.now();
        }

        RepairTask updated = RepairTask.builder()
                .id(task.getId())
                .repairOrderId(task.getRepairOrderId())
                .technicianId(task.getTechnicianId())
                .description(task.getDescription())
                .serviceId(task.getServiceId())
                .partsUsed(task.getPartsUsed())
                .status(newStatus)
                .priority(task.getPriority())
                .estimatedMinutes(task.getEstimatedMinutes())
                .actualMinutes(task.getActualMinutes())
                .notes(task.getNotes())
                .startedAt(startedAt)
                .completedAt(completedAt)
                .createdAt(task.getCreatedAt())
                .build();

        RepairTask saved = repairTaskRepository.save(updated);
        return repairTaskMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RepairTaskResponse updateTaskProgress(String taskId, Integer actualMinutes, String notes) {
        RepairTask task = repairTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("RepairTask", "id", taskId));

        RepairTask updated = RepairTask.builder()
                .id(task.getId())
                .repairOrderId(task.getRepairOrderId())
                .technicianId(task.getTechnicianId())
                .description(task.getDescription())
                .serviceId(task.getServiceId())
                .partsUsed(task.getPartsUsed())
                .status(task.getStatus())
                .priority(task.getPriority())
                .estimatedMinutes(task.getEstimatedMinutes())
                .actualMinutes(actualMinutes)
                .notes(notes)
                .startedAt(task.getStartedAt())
                .completedAt(task.getCompletedAt())
                .createdAt(task.getCreatedAt())
                .build();

        RepairTask saved = repairTaskRepository.save(updated);
        return repairTaskMapper.toResponse(saved);
    }

    @Override
    public Page<RepairTaskResponse> listTasksByTechnicianAndStatus(String technicianId, RepairTaskStatus status, Pageable pageable) {
        Page<RepairTask> page = repairTaskRepository.findByTechnicianIdAndStatus(technicianId, status, pageable);
        return page.map(repairTaskMapper::toResponse);
    }

    private boolean isValidStatusTransition(RepairTaskStatus from, RepairTaskStatus to) {
        if (to == RepairTaskStatus.BLOCKED) {
            return true;  // Can transition to BLOCKED from any state
        }

        return switch (from) {
            case ASSIGNED -> to == RepairTaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == RepairTaskStatus.COMPLETED || to == RepairTaskStatus.BLOCKED;
            case COMPLETED, BLOCKED -> false;
        };
    }
}
