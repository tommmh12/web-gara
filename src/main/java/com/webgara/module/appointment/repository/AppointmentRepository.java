package com.webgara.module.appointment.repository;

import com.webgara.module.appointment.model.Appointment;
import com.webgara.module.appointment.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    
    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    
    List<Appointment> findByCustomerId(String customerId);
    
    Page<Appointment> findByCustomerId(String customerId, Pageable pageable);
    
    Page<Appointment> findByGarageId(String garageId, Pageable pageable);
    
    List<Appointment> findByStatus(AppointmentStatus status);
    
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    
    Page<Appointment> findByGarageIdAndStatus(String garageId, AppointmentStatus status, Pageable pageable);
    
    List<Appointment> findByVehicleId(String vehicleId);
    
    Page<Appointment> findByVehicleId(String vehicleId, Pageable pageable);
    
    // Query for time slot conflicts
    @Query("{ 'garageId': ?0, 'appointmentDate': ?1, 'timeSlot.start': { $lt: ?3 }, 'timeSlot.end': { $gt: ?2 }, 'status': { $ne: 'CANCELLED' } }")
    List<Appointment> findTimeSlotConflicts(String garageId, LocalDateTime appointmentDate, String timeSlotStart, String timeSlotEnd);
    
    // Get all appointments for a garage on a specific date
    List<Appointment> findByGarageIdAndAppointmentDateAndStatus(String garageId, LocalDateTime appointmentDate, AppointmentStatus status);
    
    // Get next appointment number suffix for today
    @Query("{ 'appointmentNumber': { $regex: '^APT-?0-' } }")
    List<Appointment> findAppointmentsByDatePrefix(String datePrefix);
    
    long countByGarageIdAndAppointmentDate(String garageId, LocalDateTime appointmentDate);
}
