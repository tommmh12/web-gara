package com.webgara.module.invoice.repository;

import com.webgara.module.invoice.model.Payment;
import com.webgara.module.invoice.model.PaymentMethod;
import com.webgara.module.invoice.model.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    List<Payment> findByInvoiceId(String invoiceId);
    Page<Payment> findByInvoiceId(String invoiceId, Pageable pageable);
    Page<Payment> findByCustomerId(String customerId, Pageable pageable);
    Page<Payment> findByGarageIdOrderByPaidAtDesc(String garageId, Pageable pageable);
    Optional<Payment> findByTransactionId(String transactionId);
    Page<Payment> findByStatusAndMethod(PaymentStatus status, PaymentMethod method, Pageable pageable);
}
