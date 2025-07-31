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

    @GetMapping("/user")
    public List<Notification> getUserNotifications(@RequestParam String userId) {
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

        System.out.println("Received reply for notificationId: " + notificationId);
        System.out.println("Reply content: " + requestBody.get("reply"));

        boolean success = notificationService.addReply(notificationId, requestBody.get("reply"));

        if (success) {
            return ResponseEntity.ok("Reply added successfully");
        } else {
            System.out.println("Notification not found for ID: " + notificationId);
            return ResponseEntity.badRequest().body("Notification not found");
        }
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        boolean deleted = notificationService.deleteNotification(notificationId);
        if (deleted) {
            return ResponseEntity.ok("Notification deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
