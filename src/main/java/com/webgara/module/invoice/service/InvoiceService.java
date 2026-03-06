package com.webgara.module.invoice.service;

import com.webgara.module.invoice.dto.InvoiceRequest;
import com.webgara.module.invoice.dto.InvoiceResponse;
import com.webgara.module.invoice.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InvoiceService {
    InvoiceResponse createFromRepairOrder(String repairOrderId, InvoiceRequest request);
    InvoiceResponse getById(String id);
    Page<InvoiceResponse> listByCustomer(String customerId, Pageable pageable);
    Page<InvoiceResponse> listByGarage(String garageId, Pageable pageable);
    InvoiceResponse updateStatus(String id, InvoiceStatus newStatus);
    InvoiceResponse markAsPaid(String invoiceId);
    InvoiceResponse markAsPartiallyPaid(String invoiceId);
}
