package com.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.model.Appointment;



@Repository
public class AppointmentDaoHibernate implements AppointmentDAO {

        @Autowired
        private SessionFactory sessionFactory;

        private Session openSession() {
            return sessionFactory.openSession();
        }

        @Override
        public List<Appointment> findAll() {
            Session session = openSession();
            String sql = "SELECT * FROM appointments";
            List<Appointment> list = session.createNativeQuery(sql, Appointment.class).list();
            session.close();
            return list;
        }

        @Override
        public Appointment findById(Long id) {
            Session session = openSession();
            String sql = "SELECT * FROM appointments WHERE id = :id";
            Appointment appointment = session.createNativeQuery(sql, Appointment.class)
                                             .setParameter("id", id)
                                             .uniqueResult();
            session.close();
            return appointment;
        }

        @Override
        public Appointment save(Appointment appointment) {
            Session session = openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                session.saveOrUpdate(appointment);
                tx.commit();
                return appointment;
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
                return null;
            } finally {
                session.close();
            }
        }

        @Override
        public void deleteById(Long id) {
            Session session = openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                String sql = "DELETE FROM appointments WHERE id = :id";
                session.createNativeQuery(sql)
                       .setParameter("id", id)
                       .executeUpdate();
                tx.commit();
            } catch (Exception e) {
                if (tx != null) tx.rollback();
                e.printStackTrace();
            } finally {
                session.close();
            }
        }

        @Override
        public long count() {
            Session session = openSession();
            String sql = "SELECT COUNT(*) FROM appointments";
            Number count = (Number) session.createNativeQuery(sql).uniqueResult();
            session.close();
            return count != null ? count.longValue() : 0;
        }
        
        @Override
        public List<Appointment> findByAdvisor(String advisor) {    
            Session session = openSession();
            String sql = "SELECT * FROM appointments WHERE counselor = :advisor";
            List<Appointment> list = session.createNativeQuery(sql, Appointment.class)
                                            .setParameter("advisor", advisor)
                                            .list();
            session.close();
            return list;
        }
    }

