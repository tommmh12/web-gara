package com.webgara.module.invoice.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.invoice.dto.InvoiceRequest;
import com.webgara.module.invoice.dto.InvoiceResponse;
import com.webgara.module.invoice.mapper.InvoiceMapper;
import com.webgara.module.invoice.model.Invoice;
import com.webgara.module.invoice.model.InvoiceStatus;
import com.webgara.module.invoice.repository.InvoiceRepository;
import com.webgara.module.invoice.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceMapper invoiceMapper;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    @Transactional
    public InvoiceResponse createFromRepairOrder(String repairOrderId, InvoiceRequest request) {
        if (repairOrderId != null && invoiceRepository.findByRepairOrderId(repairOrderId).isPresent()) {
            throw new BadRequestException("Invoice already exists for this repair order");
        }

        BigDecimal subtotal = request.getItems().stream()
                .map(item -> item.getSubtotal())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal taxAmount = subtotal.multiply(request.getTaxRate().divide(new BigDecimal("100")));
        
        BigDecimal discountTotal = request.getItems().stream()
                .map(item -> item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalAmount = subtotal.add(taxAmount).subtract(discountTotal);

        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .appointmentId(request.getAppointmentId())
                .repairOrderId(repairOrderId)
                .customerId(request.getCustomerId())
                .garageId(request.getGarageId())
                .items(request.getItems().stream()
                        .map(invoiceMapper::toItemEntity)
                        .toList())
                .subtotal(subtotal)
                .taxRate(request.getTaxRate())
                .taxAmount(taxAmount)
                .discountTotal(discountTotal)
                .totalAmount(totalAmount)
                .currency("VND")
                .pdfUrl(null)
                .status(InvoiceStatus.ISSUED)
                .dueDate(request.getDueDate())
                .notes(request.getNotes())
                .issuedBy(request.getIssuedBy())
                .issuedAt(LocalDateTime.now())
                .build();

        Invoice saved = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(saved);
    }

    @Override
    public InvoiceResponse getById(String id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return invoiceMapper.toResponse(invoice);
    }

    @Override
    public Page<InvoiceResponse> listByCustomer(String customerId, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findByCustomerId(customerId, pageable);
        return page.map(invoiceMapper::toResponse);
    }

    @Override
    public Page<InvoiceResponse> listByGarage(String garageId, Pageable pageable) {
        Page<Invoice> page = invoiceRepository.findByGarageIdOrderByIssuedAtDesc(garageId, pageable);
        return page.map(invoiceMapper::toResponse);
    }

    @Override
    @Transactional
    public InvoiceResponse updateStatus(String id, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));

        if (!isValidStatusTransition(invoice.getStatus(), newStatus)) {
            throw new BadRequestException("Invalid status transition from " + invoice.getStatus() + " to " + newStatus);
        }

        invoice.setStatus(newStatus);
        Invoice updated = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public InvoiceResponse markAsPaid(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        invoice.setStatus(InvoiceStatus.PAID);
        Invoice updated = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public InvoiceResponse markAsPartiallyPaid(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        
        invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        Invoice updated = invoiceRepository.save(invoice);
        return invoiceMapper.toResponse(updated);
    }

    private String generateInvoiceNumber() {
        String datePrefix = LocalDate.now().format(DATE_FORMATTER);
        String searchPrefix = "INV-" + datePrefix + "-";

        long count = invoiceRepository.findAll().stream()
                .filter(inv -> inv.getInvoiceNumber() != null && inv.getInvoiceNumber().startsWith(searchPrefix))
                .count();

        int sequenceNumber = (int) (count + 1);
        return String.format("%s%03d", searchPrefix, sequenceNumber);
    }

    private boolean isValidStatusTransition(InvoiceStatus from, InvoiceStatus to) {
        return switch (from) {
            case DRAFT -> to == InvoiceStatus.ISSUED || to == InvoiceStatus.CANCELLED;
            case ISSUED -> to == InvoiceStatus.PAID || to == InvoiceStatus.PARTIALLY_PAID || to == InvoiceStatus.OVERDUE || to == InvoiceStatus.CANCELLED;
            case PARTIALLY_PAID -> to == InvoiceStatus.PAID || to == InvoiceStatus.OVERDUE;
            case PAID, OVERDUE, CANCELLED -> false;
        };
    }
}
