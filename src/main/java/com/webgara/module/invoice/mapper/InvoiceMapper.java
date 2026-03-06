package com.webgara.module.invoice.mapper;

import com.webgara.module.invoice.dto.InvoiceItemDTO;
import com.webgara.module.invoice.dto.InvoiceRequest;
import com.webgara.module.invoice.dto.InvoiceResponse;
import com.webgara.module.invoice.model.Invoice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InvoiceMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "invoiceNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Invoice toEntity(InvoiceRequest request);
    
    InvoiceResponse toResponse(Invoice invoice);
    InvoiceItemDTO toItemDTO(Invoice.InvoiceItem item);
    Invoice.InvoiceItem toItemEntity(InvoiceItemDTO dto);
}
