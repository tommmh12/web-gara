package com.webgara.module.repair.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.repair.dto.InspectionChecklistDTO;
import com.webgara.module.repair.dto.ProposedItemDTO;
import com.webgara.module.repair.dto.RepairOrderRequest;
import com.webgara.module.repair.dto.RepairOrderResponse;
import com.webgara.module.repair.mapper.RepairOrderMapper;
import com.webgara.module.repair.model.InspectionCondition;
import com.webgara.module.repair.model.ProposedItemType;
import com.webgara.module.repair.model.RepairOrder;
import com.webgara.module.repair.model.RepairOrderStatus;
import com.webgara.module.repair.repository.RepairOrderRepository;
import com.webgara.module.repair.service.RepairOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepairOrderServiceImpl implements RepairOrderService {

    private final RepairOrderRepository repairOrderRepository;
    private final RepairOrderMapper repairOrderMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional
    public RepairOrderResponse createFromAppointment(String appointmentId, RepairOrderRequest request) {
        if (repairOrderRepository.findByAppointmentId(appointmentId).isPresent()) {
            throw new BadRequestException("Repair order already exists for this appointment");
        }

        RepairOrder repairOrder = RepairOrder.builder()
                .appointmentId(appointmentId)
                .inspectionChecklist(new ArrayList<>())
                .proposedItems(new ArrayList<>())
                .laborCost(request.getLaborCost())
                .totalEstimate(request.getTotalEstimate())
                .customerApproved(false)
                .status(RepairOrderStatus.DRAFT)
                .repairOrderNumber(generateRepairOrderNumber())
                .build();

        RepairOrder saved = repairOrderRepository.save(repairOrder);
        return repairOrderMapper.toResponse(saved);
    }

    @Override
    public RepairOrderResponse getById(String id) {
        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairOrder", "id", id));
        return repairOrderMapper.toResponse(repairOrder);
    }

    @Override
    @Transactional
    public RepairOrderResponse updateInspection(String id, List<InspectionChecklistDTO> checklist) {
        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairOrder", "id", id));

        List<RepairOrder.InspectionChecklist> inspectionList = convertInspectionChecklistDTOs(checklist);
        
        RepairOrder updated = RepairOrder.builder()
                .id(repairOrder.getId())
                .repairOrderNumber(repairOrder.getRepairOrderNumber())
                .appointmentId(repairOrder.getAppointmentId())
                .customerId(repairOrder.getCustomerId())
                .vehicleId(repairOrder.getVehicleId())
                .garageId(repairOrder.getGarageId())
                .inspectionChecklist(inspectionList)
                .proposedItems(repairOrder.getProposedItems())
                .laborCost(repairOrder.getLaborCost())
                .totalEstimate(repairOrder.getTotalEstimate())
                .customerApproved(repairOrder.getCustomerApproved())
                .approvedAt(repairOrder.getApprovedAt())
                .status(repairOrder.getStatus())
                .startedAt(repairOrder.getStartedAt())
                .completedAt(repairOrder.getCompletedAt())
                .createdAt(repairOrder.getCreatedAt())
                .updatedAt(repairOrder.getUpdatedAt())
                .build();

        RepairOrder saved = repairOrderRepository.save(updated);
        return repairOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RepairOrderResponse updateQuote(String id, List<ProposedItemDTO> proposedItems, BigDecimal laborCost) {
        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairOrder", "id", id));

        List<RepairOrder.ProposedItem> items = convertProposedItemDTOs(proposedItems);
        BigDecimal itemsTotal = items.stream()
                .map(RepairOrder.ProposedItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalEstimate = itemsTotal.add(laborCost);

        RepairOrder updated = RepairOrder.builder()
                .id(repairOrder.getId())
                .repairOrderNumber(repairOrder.getRepairOrderNumber())
                .appointmentId(repairOrder.getAppointmentId())
                .customerId(repairOrder.getCustomerId())
                .vehicleId(repairOrder.getVehicleId())
                .garageId(repairOrder.getGarageId())
                .inspectionChecklist(repairOrder.getInspectionChecklist())
                .proposedItems(items)
                .laborCost(laborCost)
                .totalEstimate(totalEstimate)
                .customerApproved(repairOrder.getCustomerApproved())
                .approvedAt(repairOrder.getApprovedAt())
                .status(RepairOrderStatus.PENDING_APPROVAL)
                .startedAt(repairOrder.getStartedAt())
                .completedAt(repairOrder.getCompletedAt())
                .createdAt(repairOrder.getCreatedAt())
                .updatedAt(repairOrder.getUpdatedAt())
                .build();

        RepairOrder saved = repairOrderRepository.save(updated);
        return repairOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RepairOrderResponse customerApprove(String id, String customerId) {
        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairOrder", "id", id));

        if (!repairOrder.getCustomerId().equals(customerId)) {
            throw new BadRequestException("You can only approve your own repair orders");
        }

        if (repairOrder.getStatus() != RepairOrderStatus.PENDING_APPROVAL) {
            throw new BadRequestException("Only PENDING_APPROVAL orders can be approved");
        }

        RepairOrder updated = RepairOrder.builder()
                .id(repairOrder.getId())
                .repairOrderNumber(repairOrder.getRepairOrderNumber())
                .appointmentId(repairOrder.getAppointmentId())
                .customerId(repairOrder.getCustomerId())
                .vehicleId(repairOrder.getVehicleId())
                .garageId(repairOrder.getGarageId())
                .inspectionChecklist(repairOrder.getInspectionChecklist())
                .proposedItems(repairOrder.getProposedItems())
                .laborCost(repairOrder.getLaborCost())
                .totalEstimate(repairOrder.getTotalEstimate())
                .customerApproved(true)
                .approvedAt(LocalDateTime.now())
                .status(RepairOrderStatus.APPROVED)
                .startedAt(repairOrder.getStartedAt())
                .completedAt(repairOrder.getCompletedAt())
                .createdAt(repairOrder.getCreatedAt())
                .updatedAt(repairOrder.getUpdatedAt())
                .build();

        RepairOrder saved = repairOrderRepository.save(updated);
        return repairOrderMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public RepairOrderResponse updateStatus(String id, RepairOrderStatus newStatus) {
        RepairOrder repairOrder = repairOrderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RepairOrder", "id", id));

        RepairOrderStatus currentStatus = repairOrder.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        LocalDateTime startedAt = repairOrder.getStartedAt();
        LocalDateTime completedAt = repairOrder.getCompletedAt();

        if (newStatus == RepairOrderStatus.IN_PROGRESS) {
            startedAt = LocalDateTime.now();
        } else if (newStatus == RepairOrderStatus.COMPLETED) {
            completedAt = LocalDateTime.now();
        }

        RepairOrder updated = RepairOrder.builder()
                .id(repairOrder.getId())
                .repairOrderNumber(repairOrder.getRepairOrderNumber())
                .appointmentId(repairOrder.getAppointmentId())
                .customerId(repairOrder.getCustomerId())
                .vehicleId(repairOrder.getVehicleId())
                .garageId(repairOrder.getGarageId())
                .inspectionChecklist(repairOrder.getInspectionChecklist())
                .proposedItems(repairOrder.getProposedItems())
                .laborCost(repairOrder.getLaborCost())
                .totalEstimate(repairOrder.getTotalEstimate())
                .customerApproved(repairOrder.getCustomerApproved())
                .approvedAt(repairOrder.getApprovedAt())
                .status(newStatus)
                .startedAt(startedAt)
                .completedAt(completedAt)
                .createdAt(repairOrder.getCreatedAt())
                .updatedAt(repairOrder.getUpdatedAt())
                .build();

        RepairOrder saved = repairOrderRepository.save(updated);
        return repairOrderMapper.toResponse(saved);
    }

    @Override
    public Page<RepairOrderResponse> listByGarage(String garageId, Pageable pageable) {
        Page<RepairOrder> page = repairOrderRepository.findByGarageId(garageId, pageable);
        return page.map(repairOrderMapper::toResponse);
    }

    @Override
    public Page<RepairOrderResponse> listByStatus(RepairOrderStatus status, Pageable pageable) {
        Page<RepairOrder> page = repairOrderRepository.findByStatus(status, pageable);
        return page.map(repairOrderMapper::toResponse);
    }

    private List<RepairOrder.InspectionChecklist> convertInspectionChecklistDTOs(List<InspectionChecklistDTO> dtos) {
        return dtos.stream()
                .map(dto -> RepairOrder.InspectionChecklist.builder()
                        .item(dto.getItem())
                        .condition(InspectionCondition.valueOf(dto.getCondition()))
                        .note(dto.getNote())
                        .build())
                .toList();
    }

    private List<RepairOrder.ProposedItem> convertProposedItemDTOs(List<ProposedItemDTO> dtos) {
        return dtos.stream()
                .map(dto -> RepairOrder.ProposedItem.builder()
                        .type(ProposedItemType.valueOf(dto.getType()))
                        .refId(dto.getRefId())
                        .name(dto.getName())
                        .quantity(dto.getQuantity())
                        .unitPrice(dto.getUnitPrice())
                        .discount(dto.getDiscount())
                        .subtotal(dto.getSubtotal())
                        .build())
                .toList();
    }

    private String generateRepairOrderNumber() {
        String datePrefix = LocalDate.now().format(DATE_FORMATTER);
        String searchPrefix = "RO-" + datePrefix + "-";

        long count = repairOrderRepository.findAll().stream()
                .filter(ro -> ro.getRepairOrderNumber() != null && ro.getRepairOrderNumber().startsWith(searchPrefix))
                .count();

        int sequenceNumber = (int) (count + 1);
        return String.format("%s%03d", searchPrefix, sequenceNumber);
    }

    private boolean isValidStatusTransition(RepairOrderStatus from, RepairOrderStatus to) {
        return switch (from) {
            case DRAFT -> to == RepairOrderStatus.PENDING_APPROVAL;
            case PENDING_APPROVAL -> to == RepairOrderStatus.APPROVED;
            case APPROVED -> to == RepairOrderStatus.IN_PROGRESS;
            case IN_PROGRESS -> to == RepairOrderStatus.COMPLETED;
            case COMPLETED -> false;
        };
    }
}
