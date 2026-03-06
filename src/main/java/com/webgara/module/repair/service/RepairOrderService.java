package com.webgara.module.repair.service;

import com.webgara.module.repair.dto.InspectionChecklistDTO;
import com.webgara.module.repair.dto.ProposedItemDTO;
import com.webgara.module.repair.dto.RepairOrderRequest;
import com.webgara.module.repair.dto.RepairOrderResponse;
import com.webgara.module.repair.model.RepairOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface RepairOrderService {
    RepairOrderResponse createFromAppointment(String appointmentId, RepairOrderRequest request);
    RepairOrderResponse getById(String id);
    RepairOrderResponse updateInspection(String id, List<InspectionChecklistDTO> checklist);
    RepairOrderResponse updateQuote(String id, List<ProposedItemDTO> proposedItems, BigDecimal laborCost);
    RepairOrderResponse customerApprove(String id, String customerId);
    RepairOrderResponse updateStatus(String id, RepairOrderStatus newStatus);
    Page<RepairOrderResponse> listByGarage(String garageId, Pageable pageable);
    Page<RepairOrderResponse> listByStatus(RepairOrderStatus status, Pageable pageable);
}
