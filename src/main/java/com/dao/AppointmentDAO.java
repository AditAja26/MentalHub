package com.dao;

import java.util.List;

import com.model.Appointment;

public interface AppointmentDao {
    Appointment save(Appointment appointment);
    List<Appointment> findAll();
    Appointment findById(Long id);
    List<Appointment> findByAdvisor(String advisor);
    void deleteById(Long id);
    long count();
}
