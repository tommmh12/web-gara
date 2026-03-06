package com.webgara.module.invoice.service;

import com.webgara.module.invoice.dto.PaymentCallbackRequest;
import com.webgara.module.invoice.dto.PaymentRequest;
import com.webgara.module.invoice.dto.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    
    PaymentResponse processCallback(PaymentCallbackRequest callback);
    
    Page<PaymentResponse> listByInvoice(String invoiceId, Pageable pageable);
    
    Page<PaymentResponse> listByCustomer(String customerId, Pageable pageable);
    
    Page<PaymentResponse> listByGarage(String garageId, Pageable pageable);
    
    String mockVNPayUrl(String invoiceId, BigDecimal amount);
    
    String mockMoMoUrl(String invoiceId, BigDecimal amount);
}
