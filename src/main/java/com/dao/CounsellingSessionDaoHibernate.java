package com.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.model.CounsellingSession;
import java.util.List;

import com.dao.CounsellingSessionDao;

@Repository
public class CounsellingSessionDaoHibernate implements CounsellingSessionDao {

    @Autowired
    private SessionFactory sessionFactory;

    private Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public CounsellingSession save(CounsellingSession session) {
        Session sessionH = openSession();
        try {
            sessionH.beginTransaction();
            sessionH.saveOrUpdate(session);
            sessionH.getTransaction().commit();
            return session;
        } catch (Exception e) {
            e.printStackTrace();
            if (sessionH.getTransaction() != null) {
                sessionH.getTransaction().rollback();
            }
            return null;
        } finally {
            sessionH.close();
        }
    }

    @Override
    public CounsellingSession findById(Long id) {
        Session sessionH = openSession();
        try {
            return sessionH.get(CounsellingSession.class, id);
        } finally {
            sessionH.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        Session sessionH = openSession();
        try {
            sessionH.beginTransaction();
            CounsellingSession session = sessionH.get(CounsellingSession.class, id);
            if (session != null) {
                sessionH.delete(session);
            }
            sessionH.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (sessionH.getTransaction() != null) {
                sessionH.getTransaction().rollback();
            }
        } finally {
            sessionH.close();
        }
    }

    @Override
    public List<CounsellingSession> findAll() {
        Session sessionH = openSession();
        try {
            return sessionH.createQuery("from CounsellingSession", CounsellingSession.class).list();
        } finally {
            sessionH.close();
        }
    }

    @Override
    public List<CounsellingSession> findByDate(String dateString) {
        Session sessionH = openSession();
        try {
            String hql = "from CounsellingSession where date = :dateString";
            return sessionH.createQuery(hql, CounsellingSession.class)
                    .setParameter("dateString", dateString)
                    .list();
        } finally {
            sessionH.close();
        }
    }
}
