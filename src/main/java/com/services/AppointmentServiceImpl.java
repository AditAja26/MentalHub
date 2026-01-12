package com.services;

import java.util.List;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.services.AppointmentService;
import com.dao.AppointmentDao;
import com.model.Appointment;


@Service
@Transactional
class AppointmentServiceImpl implements AppointmentService {
    
    @Autowired
    private AppointmentDao appointmentDao;

    @PostConstruct
    public void init() {
        if (appointmentDao.count() == 0) {
            Appointment appointment = new Appointment();
            appointment.setId(1L);
            appointment.setCounselor("Dr. Smith");
            appointment.setDate("2024-07-15");
            appointment.setTime("10:00");
            appointment.setReason("Completed");
            appointmentDao.save(appointment);
        }
    }

    @Override
    public Appointment addAppointment(Appointment appointment) {
        return appointmentDao.save(appointment);
    }

    @Override
    public List<Appointment> getAllAppointments() {
        return appointmentDao.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentDao.findById(id);
    }

    @Override
    public List<Appointment> getAppointmentsByAdvisor(String advisor) {
        return appointmentDao.findByAdvisor(advisor);
    }

    @Override
    public Appointment updateAppointment(Long id, Appointment updatedAppointment) {
        Appointment existing = appointmentDao.findById(id);
        
        if (existing != null) {
            if (updatedAppointment.getCounselor() != null) 
                existing.setCounselor(updatedAppointment.getCounselor());
            
            if (updatedAppointment.getDate() != null) 
                existing.setDate(updatedAppointment.getDate());
            
            if (updatedAppointment.getTime() != null) 
                existing.setTime(updatedAppointment.getTime());
            
            if (updatedAppointment.getReason() != null) 
                existing.setReason(updatedAppointment.getReason());

            return appointmentDao.save(existing);
        }
        return null;
    }


    @Override
    public boolean deleteAppointment(Long id) {
        Appointment existing = appointmentDao.findById(id);
        if (existing != null) {
            appointmentDao.deleteById(id);
            return true;
        }
        return false;
    }
}