package com.webgara.module.invoice.service.impl;

import com.webgara.common.exception.BadRequestException;
import com.webgara.common.exception.ResourceNotFoundException;
import com.webgara.module.invoice.dto.PaymentCallbackRequest;
import com.webgara.module.invoice.dto.PaymentRequest;
import com.webgara.module.invoice.dto.PaymentResponse;
import com.webgara.module.invoice.mapper.PaymentMapper;
import com.webgara.module.invoice.model.Invoice;
import com.webgara.module.invoice.model.Payment;
import com.webgara.module.invoice.model.PaymentMethod;
import com.webgara.module.invoice.model.PaymentStatus;
import com.webgara.module.invoice.repository.InvoiceRepository;
import com.webgara.module.invoice.repository.PaymentRepository;
import com.webgara.module.invoice.service.InvoiceService;
import com.webgara.module.invoice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", request.getInvoiceId()));

        String transactionId = request.getTransactionId() != null ? request.getTransactionId() : UUID.randomUUID().toString();
        PaymentStatus status = PaymentStatus.PENDING;
        LocalDateTime paidAt = null;

        if (request.getMethod() == PaymentMethod.CASH || request.getMethod() == PaymentMethod.BANK_TRANSFER) {
            status = PaymentStatus.SUCCESS;
            paidAt = LocalDateTime.now();
        }

        Payment payment = Payment.builder()
                .invoiceId(request.getInvoiceId())
                .customerId(request.getCustomerId())
                .garageId(request.getGarageId())
                .amount(request.getAmount())
                .currency("VND")
                .method(request.getMethod())
                .status(status)
                .transactionId(transactionId)
                .bankInfo(request.getBankInfo() != null ? paymentMapper.toBankInfoEntity(request.getBankInfo()) : null)
                .paidAt(paidAt)
                .build();

        Payment saved = paymentRepository.save(payment);

        if (status == PaymentStatus.SUCCESS) {
            BigDecimal totalPaid = paymentRepository.findByInvoiceId(request.getInvoiceId()).stream()
                    .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoiceService.markAsPaid(request.getInvoiceId());
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                invoiceService.markAsPartiallyPaid(request.getInvoiceId());
            }
        }

        PaymentResponse response = paymentMapper.toResponse(saved);
        
        if (request.getMethod() == PaymentMethod.VNPAY) {
            response.setPaymentUrl(mockVNPayUrl(request.getInvoiceId(), request.getAmount()));
        } else if (request.getMethod() == PaymentMethod.MOMO) {
            response.setPaymentUrl(mockMoMoUrl(request.getInvoiceId(), request.getAmount()));
        }

        return response;
    }

    @Override
    @Transactional
    public PaymentResponse processCallback(PaymentCallbackRequest callback) {
        Payment payment = paymentRepository.findByTransactionId(callback.getTransactionId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "transactionId", callback.getTransactionId()));

        payment.setStatus(callback.getStatus());
        payment.setGatewayResponse(callback.getGatewayResponse());

        if (callback.getStatus() == PaymentStatus.SUCCESS) {
            payment.setPaidAt(LocalDateTime.now());
            
            Invoice invoice = invoiceRepository.findById(payment.getInvoiceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", payment.getInvoiceId()));
            
            BigDecimal totalPaid = paymentRepository.findByInvoiceId(payment.getInvoiceId()).stream()
                    .filter(p -> p.getStatus() == PaymentStatus.SUCCESS)
                    .map(Payment::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (totalPaid.compareTo(invoice.getTotalAmount()) >= 0) {
                invoiceService.markAsPaid(payment.getInvoiceId());
            } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
                invoiceService.markAsPartiallyPaid(payment.getInvoiceId());
            }
        }

        Payment updated = paymentRepository.save(payment);
        return paymentMapper.toResponse(updated);
    }

    @Override
    public List<PaymentResponse> listByInvoice(String invoiceId) {
        return paymentRepository.findByInvoiceId(invoiceId).stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Override
    public Page<PaymentResponse> listByCustomer(String customerId, Pageable pageable) {
        Page<Payment> page = paymentRepository.findByCustomerId(customerId, pageable);
        return page.map(paymentMapper::toResponse);
    }

    @Override
    public Page<PaymentResponse> listByGarage(String garageId, Pageable pageable) {
        Page<Payment> page = paymentRepository.findByGarageIdOrderByPaidAtDesc(garageId, pageable);
        return page.map(paymentMapper::toResponse);
    }

    @Override
    public String mockVNPayUrl(String invoiceId, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        return String.format("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=%s&vnp_OrderInfo=INV-%s&vnp_TxnRef=%s",
                amount.multiply(new BigDecimal("100")).longValue(),
                invoice.getInvoiceNumber(),
                UUID.randomUUID().toString());
    }

    @Override
    public String mockMoMoUrl(String invoiceId, BigDecimal amount) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));
        return String.format("https://test-payment.momo.vn/gw_payment/transactionProcessor?amount=%s&orderId=INV-%s&requestId=%s",
                amount.longValue(),
                invoice.getInvoiceNumber(),
                UUID.randomUUID().toString());
    }
}
