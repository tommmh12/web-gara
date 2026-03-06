package com.webgara.module.invoice.mapper;

import com.webgara.module.invoice.dto.BankInfoDTO;
import com.webgara.module.invoice.dto.PaymentRequest;
import com.webgara.module.invoice.dto.PaymentResponse;
import com.webgara.module.invoice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "gatewayResponse", ignore = true)
    @Mapping(target = "paidAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Payment toEntity(PaymentRequest request);
    
    PaymentResponse toResponse(Payment payment);
    Payment.BankInfo toBankInfoEntity(BankInfoDTO dto);
    BankInfoDTO toBankInfoDTO(Payment.BankInfo entity);
}
