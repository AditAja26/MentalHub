package com.dao;

import com.model.Notification;
import java.util.List;

public interface NotificationDAO {
    void save(Notification notification);
    Notification getById(Long id);
    List<Notification> getByUserId(Long userId);
    List<Notification> getUnreadByUserId(Long userId);
    void markAsRead(Long notificationId);
    void delete(Long id);
    long countUnreadByUserId(Long userId);
}

