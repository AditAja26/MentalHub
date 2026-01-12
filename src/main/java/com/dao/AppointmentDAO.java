package com.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.model.Appointment;

@Repository 
public class AppointmentDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void save(Appointment appointment) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(appointment);
    }
}