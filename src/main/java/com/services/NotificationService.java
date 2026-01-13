package com.services;

import com.dao.NotificationDAO;
import com.model.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationDAO notificationDAO;
    
    @Transactional
    public void createNotification(Long userId, String title, String message, String type) {
        Notification notification = new Notification(userId, title, message, type);
        notificationDAO.save(notification);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId) {
        return notificationDAO.getByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationDAO.getUnreadByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationDAO.countUnreadByUserId(userId);
    }
    
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationDAO.markAsRead(notificationId);
    }
    
    @Transactional
    public void deleteNotification(Long id) {
        notificationDAO.delete(id);
    }
}

