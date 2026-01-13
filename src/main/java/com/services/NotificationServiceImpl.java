package com.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dao.NotificationDao;
import com.model.Notification;


@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationDao notificationDao;

    @Override
    @Transactional
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationDao.getNotificationsByUserId(userId);
    }

    @Override
    public long getUnreadCount(List<Notification> list) {
        return list.stream().filter(n -> !n.isRead()).count();
    }

    @Override
    public long getDirectCount(List<Notification> list) {
        return list.stream().filter(n -> "DIRECT".equalsIgnoreCase(n.getType())).count();
    }

    @Override
    @Transactional
    public List<Notification> getNotifications() {
        return notificationDao.getNotifications();
    }
}