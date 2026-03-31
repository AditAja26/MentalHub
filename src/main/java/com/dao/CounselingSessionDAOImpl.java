package com.dao;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.model.CounselingSession;
import com.model.User;

@Repository
public class CounselingSessionDAOImpl implements CounselingSessionDAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void addSession(CounselingSession session) {
        getCurrentSession().save(session);
    }

    @Override
    public void updateSession(CounselingSession session) {
        getCurrentSession().update(session);
    }

    @Override
    public void deleteSession(Long id) {
        CounselingSession session = getSessionById(id);
        if (session != null) {
            getCurrentSession().delete(session);
        }
    }

    @Override
    public CounselingSession getSessionById(Long id) {
        return getCurrentSession().get(CounselingSession.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CounselingSession> getAllSessions() {
        return getCurrentSession().createQuery("from CounselingSession order by date asc, time asc").list();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<CounselingSession> getSessionsByAdvisor(User advisor) {
        return getCurrentSession()
                .createQuery("from CounselingSession where advisor.id = :advisorId order by date desc")
                .setParameter("advisorId", advisor.getId())
                .list();
    }
}