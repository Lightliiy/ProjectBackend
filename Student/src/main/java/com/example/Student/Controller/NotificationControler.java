package com.example.Student.Controller;

import com.example.Student.Model.Notification;
import com.example.Student.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationControler {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable String userId) {
        return notificationService.getUserNotifications(userId);
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

    // ✅ New: Reply to a notification
    @PostMapping("/{notificationId}/reply")
    public ResponseEntity<String> replyToNotification(
            @PathVariable Long notificationId,
            @RequestBody Map<String, String> requestBody) {

        String reply = requestBody.get("reply");
        boolean success = notificationService.addReply(notificationId, reply);

        if (success) {
            return ResponseEntity.ok("Reply added successfully");
        } else {
            return ResponseEntity.badRequest().body("Notification not found");
        }
    }
}
