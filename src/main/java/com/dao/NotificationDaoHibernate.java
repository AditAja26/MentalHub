package com.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.model.Notification;


@Repository
public class NotificationDaoHibernate implements NotificationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        Session currentSession = sessionFactory.getCurrentSession();
        // Filter by user.id
        Query<Notification> theQuery = currentSession.createQuery(
            "from Notification where user.id = :uId order by dateCreated desc", Notification.class);
        theQuery.setParameter("uId", userId);
        
        return theQuery.getResultList();
    }
    
    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        Session currentSession = sessionFactory.getCurrentSession();
        // Filter by user.id
        Query<Notification> theQuery = currentSession.createQuery(
            "from Notification where user.id = :uId order by dateCreated desc", Notification.class);
        theQuery.setParameter("uId", userId);
        
        return theQuery.getResultList();
    }

    @Override
    public List<Notification> getNotifications() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Notification> theQuery = currentSession.createQuery("from Notification order by dateCreated desc", Notification.class);
        return theQuery.getResultList();
    }

    @Override
    public List<Notification> getAllNotifications() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Notification> theQuery = currentSession.createQuery("from Notification order by dateCreated desc", Notification.class);
        return theQuery.getResultList();
    }

    @Override
    public List<Notification> findAll() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Notification> theQuery = currentSession.createQuery("from Notification", Notification.class);
        return theQuery.getResultList();
    }

    @Override
    public Notification findById(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        return currentSession.get(Notification.class, id);
    }
}