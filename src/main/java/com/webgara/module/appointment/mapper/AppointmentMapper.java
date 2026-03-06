package com.webgara.module.appointment.mapper;

import com.webgara.module.appointment.dto.AppointmentRequest;
import com.webgara.module.appointment.dto.AppointmentResponse;
import com.webgara.module.appointment.model.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AppointmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointmentNumber", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "assignedReceptionistId", ignore = true)
    @Mapping(target = "estimatedCost", ignore = true)
    @Mapping(target = "staffNotes", ignore = true)
    @Mapping(target = "cancelReason", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Appointment toEntity(AppointmentRequest request);

    AppointmentResponse toResponse(Appointment entity);

    List<AppointmentResponse> toResponseList(List<Appointment> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "appointmentNumber", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "statusHistory", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(AppointmentRequest request, @MappingTarget Appointment entity);
}
