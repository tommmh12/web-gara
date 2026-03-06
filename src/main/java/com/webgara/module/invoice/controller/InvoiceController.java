package com.webgara.module.invoice.controller;

import com.webgara.module.invoice.dto.InvoiceRequest;
import com.webgara.module.invoice.dto.InvoiceResponse;
import com.webgara.module.invoice.model.InvoiceStatus;
import com.webgara.module.invoice.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<InvoiceResponse> createInvoice(@Valid @RequestBody InvoiceRequest request) {
        InvoiceResponse response = invoiceService.createFromRepairOrder(request.getRepairOrderId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable String id) {
        InvoiceResponse response = invoiceService.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<InvoiceResponse>> listMyInvoices(
            @RequestParam String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InvoiceResponse> invoices = invoiceService.listByCustomer(customerId, pageable);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<Page<InvoiceResponse>> listInvoices(
            @RequestParam String garageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InvoiceResponse> invoices = invoiceService.listByGarage(garageId, pageable);
        return ResponseEntity.ok(invoices);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<InvoiceResponse> updateInvoiceStatus(
            @PathVariable String id,
            @RequestParam InvoiceStatus newStatus) {
        InvoiceResponse response = invoiceService.updateStatus(id, newStatus);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/mark-as-paid")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<InvoiceResponse> markAsPaid(@PathVariable String id) {
        InvoiceResponse response = invoiceService.markAsPaid(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/mark-as-partially-paid")
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<InvoiceResponse> markAsPartiallyPaid(@PathVariable String id) {
        InvoiceResponse response = invoiceService.markAsPartiallyPaid(id);
        return ResponseEntity.ok(response);
    }
}
