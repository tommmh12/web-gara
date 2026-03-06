package com.webgara.module.repair.repository;

import com.webgara.module.repair.model.RepairOrder;
import com.webgara.module.repair.model.RepairOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepairOrderRepository extends MongoRepository<RepairOrder, String> {
    Optional<RepairOrder> findByRepairOrderNumber(String repairOrderNumber);
    Optional<RepairOrder> findByAppointmentId(String appointmentId);
    Page<RepairOrder> findByGarageId(String garageId, Pageable pageable);
    Page<RepairOrder> findByStatus(RepairOrderStatus status, Pageable pageable);
}
