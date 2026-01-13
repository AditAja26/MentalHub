package com.services;

import java.util.List;

import com.model.Appointment;

public interface AppointmentService {
    Appointment addAppointment(Appointment appointment);
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    List<Appointment> getAppointmentsByAdvisor(String advisor);
    Appointment updateAppointment(Long id, Appointment updatedAppointment);
    boolean deleteAppointment(Long id);
}