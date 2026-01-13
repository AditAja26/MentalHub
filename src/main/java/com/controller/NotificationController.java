package com.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.model.Notification;
import com.services.NotificationService;


@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification")
    public String showNotifications(HttpSession session, Model theModel) {
        // 1. Get current user ID from session
        Long currentUserId = (Long) session.getAttribute("userId");
        
        if (currentUserId == null) {
            return "redirect:/login"; // Safety check
        }

        // 2. Fetch notifications only for this user
        List<Notification> notifications = notificationService.getNotificationsForUser(currentUserId);
        
        // 3. Populate model
        theModel.addAttribute("notifications", notifications);
        theModel.addAttribute("totalCount", notifications.size());
        theModel.addAttribute("unreadCount", notificationService.getUnreadCount(notifications));
        theModel.addAttribute("directCount", notificationService.getDirectCount(notifications));
        
        return "Notification"; 
    }
}