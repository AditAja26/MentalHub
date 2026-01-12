package com.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dao.AppointmentDAO;
import com.model.Appointment;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentDAO appointmentDAO;

    @Transactional
    public void saveAppointment(Appointment appointment) {
        appointmentDAO.save(appointment);
    }
}