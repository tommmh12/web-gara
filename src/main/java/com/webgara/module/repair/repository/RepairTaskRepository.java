package com.webgara.module.repair.repository;

import com.webgara.module.repair.model.RepairTask;
import com.webgara.module.repair.model.RepairTaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RepairTaskRepository extends MongoRepository<RepairTask, String> {
    List<RepairTask> findByRepairOrderId(String repairOrderId);
    Page<RepairTask> findByTechnicianIdAndStatus(String technicianId, RepairTaskStatus status, Pageable pageable);
    Page<RepairTask> findByStatus(RepairTaskStatus status, Pageable pageable);
}
