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

    // Get notifications for a specific user
    @GetMapping("/user")
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestParam String userId) {
        System.out.println("Fetching notifications for userId: " + userId);
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }


    @PostMapping
    public ResponseEntity<Notification> createNotification(@RequestBody Notification notification) {
        System.out.println("Creating notification for userId: " + notification.getUserId());
        Notification created = notificationService.createNotification(
                notification.getUserId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType()
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/markAllRead/{userId}")
    public ResponseEntity<String> markAllAsRead(@PathVariable String userId) {
        System.out.println("Marking all notifications as read for userId: " + userId);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }

    @PostMapping("/{notificationId}/reply")
    public ResponseEntity<String> replyToNotification(
            @PathVariable Long notificationId,
            @RequestBody Map<String, String> requestBody) {

        System.out.println("Received reply for notificationId: " + notificationId);

        String replyMessage = requestBody.get("reply");
        if (replyMessage == null || replyMessage.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Reply message cannot be empty");
        }

        boolean success = notificationService.addReply(notificationId, replyMessage);

        if (success) {
            System.out.println("Reply added successfully for notificationId: " + notificationId);
            return ResponseEntity.ok("Reply added successfully");
        } else {
            System.out.println("Notification not found for ID: " + notificationId);
            return ResponseEntity.status(404).body("Notification not found");
        }
    }

    // ✅ Delete a notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long notificationId) {
        System.out.println("Deleting notification with ID: " + notificationId);
        boolean deleted = notificationService.deleteNotification(notificationId);
        if (deleted) {
            return ResponseEntity.ok("Notification deleted successfully");
        } else {
            return ResponseEntity.status(404).body("Notification not found");
        }
    }
}
