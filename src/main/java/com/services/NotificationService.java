package com.services;

import java.util.List;

import com.model.Notification;

public interface NotificationService {
    List<Notification> getNotificationsForUser(Long userId);
    List<Notification> getNotifications();
    long getUnreadCount(List<Notification> list);
    long getDirectCount(List<Notification> list);
}