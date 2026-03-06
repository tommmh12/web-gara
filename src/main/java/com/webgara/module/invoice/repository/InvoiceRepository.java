package com.webgara.module.invoice.repository;

import com.webgara.module.invoice.model.Invoice;
import com.webgara.module.invoice.model.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    Page<Invoice> findByCustomerId(String customerId, Pageable pageable);
    Page<Invoice> findByGarageIdOrderByIssuedAtDesc(String garageId, Pageable pageable);
    Page<Invoice> findByStatus(InvoiceStatus status, Pageable pageable);
    Optional<Invoice> findByAppointmentId(String appointmentId);
    Optional<Invoice> findByRepairOrderId(String repairOrderId);
}
