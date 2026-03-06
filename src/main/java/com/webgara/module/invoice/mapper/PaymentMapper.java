package com.webgara.module.invoice.mapper;

import com.webgara.module.invoice.dto.BankInfoDTO;
import com.webgara.module.invoice.dto.PaymentRequest;
import com.webgara.module.invoice.dto.PaymentResponse;
import com.webgara.module.invoice.model.Payment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    Payment toEntity(PaymentRequest request);
    PaymentResponse toResponse(Payment payment);
    Payment.BankInfo toBankInfoEntity(BankInfoDTO dto);
    BankInfoDTO toBankInfoDTO(Payment.BankInfo entity);
}
