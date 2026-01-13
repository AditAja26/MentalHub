package com.dao;

import java.util.List;

import com.model.Notification;

public interface NotificationDao {
    List<Notification> getNotifications();
    List<Notification> getNotificationsByUserId(Long userId);
    List<Notification> getAllNotifications();
    List<Notification> getNotificationsForUser(Long userId);         
    List<Notification> findAll();                
    Notification findById(Long id);                        
}