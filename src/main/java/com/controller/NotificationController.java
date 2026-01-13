package com.controller;

import com.model.Notification;
import com.model.User;
import com.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping
    public String showNotifications(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        
        // Get all notifications for the user
        List<Notification> allNotifications = notificationService.getUserNotifications(user.getId());
        List<Notification> unreadNotifications = notificationService.getUnreadNotifications(user.getId());
        long unreadCount = notificationService.getUnreadCount(user.getId());
        
        model.addAttribute("notifications", allNotifications);
        model.addAttribute("totalMessages", allNotifications.size());
        model.addAttribute("unreadMessages", unreadCount);
        model.addAttribute("directMessages", 0); // Can be enhanced later
        
        // Update unread count in session
        session.setAttribute("unreadCount", unreadCount);
        
        return "notificationModule/NotificationPage";
    }
    
    @GetMapping("/mark-read/{id}")
    public String markAsRead(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }
}

