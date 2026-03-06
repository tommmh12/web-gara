package com.webgara.module.inventory.mapper;

import com.webgara.module.inventory.dto.CompatibleVehiclesDTO;
import com.webgara.module.inventory.dto.PartRequest;
import com.webgara.module.inventory.dto.PartResponse;
import com.webgara.module.inventory.dto.SupplierDTO;
import com.webgara.module.inventory.model.Part;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PartMapper {
    Part toEntity(PartRequest request);

    PartResponse toResponse(Part part);

    List<PartResponse> toResponseList(List<Part> parts);

    default Part.CompatibleVehicles compatibleVehiclesDTOToCompatibleVehicles(CompatibleVehiclesDTO dto) {
        if (dto == null) {
            return null;
        }
        return Part.CompatibleVehicles.builder()
                .brand(dto.getBrand())
                .models(dto.getModels())
                .build();
    }

    default CompatibleVehiclesDTO compatibleVehiclesToDTO(Part.CompatibleVehicles vehicles) {
        if (vehicles == null) {
            return null;
        }
        return CompatibleVehiclesDTO.builder()
                .brand(vehicles.getBrand())
                .models(vehicles.getModels())
                .build();
    }

    default Part.Supplier supplierDTOToSupplier(SupplierDTO dto) {
        if (dto == null) {
            return null;
        }
        return Part.Supplier.builder()
                .name(dto.getName())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .build();
    }

    default SupplierDTO supplierToDTO(Part.Supplier supplier) {
        if (supplier == null) {
            return null;
        }
        return SupplierDTO.builder()
                .name(supplier.getName())
                .phone(supplier.getPhone())
                .email(supplier.getEmail())
                .build();
    }
}
