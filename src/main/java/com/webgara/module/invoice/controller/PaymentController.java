package com.webgara.module.invoice.controller;

import com.webgara.module.invoice.dto.PaymentCallbackRequest;
import com.webgara.module.invoice.dto.PaymentRequest;
import com.webgara.module.invoice.dto.PaymentResponse;
import com.webgara.module.invoice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/callback")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> paymentCallback(@Valid @RequestBody PaymentCallbackRequest callback) {
        paymentService.processCallback(callback);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Payment callback processed");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoice/{invoiceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentResponse>> listPaymentsByInvoice(
            @PathVariable String invoiceId) {
        List<PaymentResponse> payments = paymentService.listByInvoice(invoiceId);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Page<PaymentResponse>> listMyPayments(
            @RequestParam String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponse> payments = paymentService.listByCustomer(customerId, pageable);
        return ResponseEntity.ok(payments);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('RECEPTIONIST', 'MANAGER')")
    public ResponseEntity<Page<PaymentResponse>> listPayments(
            @RequestParam String garageId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentResponse> payments = paymentService.listByGarage(garageId, pageable);
        return ResponseEntity.ok(payments);
    }
}
