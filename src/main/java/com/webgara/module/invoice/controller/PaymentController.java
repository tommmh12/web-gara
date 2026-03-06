package com.webgara.module.invoice.controller;

import com.webgara.module.invoice.dto.PaymentCallbackRequest;
import com.webgara.module.invoice.dto.PaymentRequest;
import com.webgara.module.invoice.dto.PaymentResponse;
import com.webgara.module.invoice.service.PaymentService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    @PostMapping("/callback")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PaymentResponse> paymentCallback(@Valid @RequestBody PaymentCallbackRequest callback) {
        return ResponseEntity.ok(paymentService.processCallback(callback));
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PaymentResponse>> listPaymentsByInvoice(@PathVariable String invoiceId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paymentService.listByInvoice(invoiceId, pageable));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<PaymentResponse>> listMyPayments(@RequestParam String customerId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paymentService.listByCustomer(customerId, pageable));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<Page<PaymentResponse>> listPayments(@RequestParam String garageId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(paymentService.listByGarage(garageId, pageable));
    }
}
