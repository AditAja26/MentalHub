package com.dao;

import com.model.MoodLog;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class MoodLogDAOHibernate implements MoodLogDAO {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public void save(MoodLog moodLog) {
        System.out.println(">>> MoodLogDAOHibernate.save() called");
        System.out.println(">>> MoodLog before save - ID: " + moodLog.getId());
        System.out.println(">>> MoodLog.user before save: " + (moodLog.getUser() != null ? 
                           "User ID: " + moodLog.getUser().getId() + ", Name: " + moodLog.getUser().getName() : "NULL"));
        System.out.println(">>> MoodLog.score: " + moodLog.getScore());
        System.out.println(">>> MoodLog.moodType: " + moodLog.getMoodType());
        System.out.println(">>> MoodLog.q1: " + moodLog.getQ1OverallFeeling());
        
        try {
            sessionFactory.getCurrentSession().saveOrUpdate(moodLog);
            System.out.println(">>> Hibernate saveOrUpdate completed successfully");
            System.out.println(">>> MoodLog after save - ID: " + moodLog.getId());
        } catch (Exception e) {
            System.out.println(">>> ERROR in Hibernate save: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public MoodLog getById(Long id) {
        return sessionFactory.getCurrentSession().get(MoodLog.class, id);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MoodLog> getByUserId(Long userId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM MoodLog m WHERE m.user.id = :uid ORDER BY m.loggedAt DESC")
                .setParameter("uid", userId)
                .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<MoodLog> getByUserIdAndDateRange(Long userId, Date startDate, Date endDate) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM MoodLog m WHERE m.user.id = :uid AND m.loggedAt BETWEEN :start AND :end ORDER BY m.loggedAt DESC")
                .setParameter("uid", userId)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
    }

    @Override
    public MoodLog getLatestByUserId(Long userId) {
        Query<MoodLog> query = sessionFactory.getCurrentSession()
                .createQuery("FROM MoodLog m WHERE m.user.id = :uid ORDER BY m.loggedAt DESC", MoodLog.class)
                .setParameter("uid", userId)
                .setMaxResults(1);
        
        List<MoodLog> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public boolean hasLoggedMoodToday(Long userId) {
        // Get start of today (00:00:00)
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();
        
        // Get end of today (23:59:59)
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfDay = calendar.getTime();
        
        Long count = (Long) sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(m) FROM MoodLog m WHERE m.user.id = :uid AND m.loggedAt BETWEEN :start AND :end AND m.moodType IS NOT NULL")
                .setParameter("uid", userId)
                .setParameter("start", startOfDay)
                .setParameter("end", endOfDay)
                .uniqueResult();
        
        return count != null && count > 0;
    }

    @Override
    public void update(MoodLog moodLog) {
        sessionFactory.getCurrentSession().update(moodLog);
    }

    @Override
    public void delete(Long id) {
        MoodLog moodLog = getById(id);
        if (moodLog != null) {
            sessionFactory.getCurrentSession().delete(moodLog);
        }
    }
}
