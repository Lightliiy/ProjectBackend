package com.example.Student.Controller;


import com.example.Student.Model.Notification;
import com.example.Student.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationControler {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable String studentId) {
        return notificationService.getUserNotifications(studentId);
    }

    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(
                notification.getUserId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType()
        );
    }

    @PutMapping("/markAllRead/{userId}")
    public void markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
    }
}