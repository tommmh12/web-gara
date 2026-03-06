package com.webgara.module.invoice.mapper;

import com.webgara.module.invoice.dto.InvoiceItemDTO;
import com.webgara.module.invoice.dto.InvoiceRequest;
import com.webgara.module.invoice.dto.InvoiceResponse;
import com.webgara.module.invoice.model.Invoice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface InvoiceMapper {
    Invoice toEntity(InvoiceRequest request);
    InvoiceResponse toResponse(Invoice invoice);
    InvoiceItemDTO toItemDTO(Invoice.InvoiceItem item);
    Invoice.InvoiceItem toItemEntity(InvoiceItemDTO dto);
}
