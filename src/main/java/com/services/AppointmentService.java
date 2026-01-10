package com.services;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.model.Appointment;

@Service
public class AppointmentService {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void saveAppointment(Appointment appointment) {
        sessionFactory.getCurrentSession().save(appointment);
    }
}