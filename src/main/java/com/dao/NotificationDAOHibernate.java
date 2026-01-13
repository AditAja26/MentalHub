package com.dao;

import com.model.Notification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NotificationDAOHibernate implements NotificationDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    @Override
    public void save(Notification notification) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(notification);
    }
    
    @Override
    public Notification getById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Notification.class, id);
    }
    
    @Override
    public List<Notification> getByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Notification WHERE userId = :userId ORDER BY createdAt DESC", Notification.class)
                .setParameter("userId", userId)
                .list();
    }
    
    @Override
    public List<Notification> getUnreadByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Notification WHERE userId = :userId AND isRead = false ORDER BY createdAt DESC", Notification.class)
                .setParameter("userId", userId)
                .list();
    }
    
    @Override
    public void markAsRead(Long notificationId) {
        Session session = sessionFactory.getCurrentSession();
        Notification notification = session.get(Notification.class, notificationId);
        if (notification != null) {
            notification.setRead(true);
            session.update(notification);
        }
    }
    
    @Override
    public void delete(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Notification notification = session.get(Notification.class, id);
        if (notification != null) {
            session.delete(notification);
        }
    }
    
    @Override
    public long countUnreadByUserId(Long userId) {
        Session session = sessionFactory.getCurrentSession();
        return (long) session.createQuery("SELECT COUNT(*) FROM Notification WHERE userId = :userId AND isRead = false")
                .setParameter("userId", userId)
                .uniqueResult();
    }
}

