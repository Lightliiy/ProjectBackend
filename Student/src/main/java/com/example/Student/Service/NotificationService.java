package com.example.Student.Service;

import com.example.Student.Model.Notification;
import com.example.Student.Repository.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    public Notification createNotification(String userId, String title, String message, String type) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        return notificationRepo.save(notification);
    }

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepo.findByUserIdOrderByTimestampDesc(userId);
    }

    public void markAllAsRead(String userId) {
        List<Notification> notifications = notificationRepo.findByUserIdOrderByTimestampDesc(userId);
        notifications.forEach(n -> n.setRead(true));
        notificationRepo.saveAll(notifications);
    }

    // ✅ New: Add reply to a notification
    public boolean addReply(Long notificationId, String reply) {
        Optional<Notification> optionalNotification = notificationRepo.findById(notificationId);
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setReply(reply);
            notificationRepo.save(notification);
            return true;
        }
        return false;
    }

    public boolean deleteNotification(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepo.findById(notificationId);
        if (notificationOpt.isPresent()) {
            notificationRepo.deleteById(notificationId);
            return true;
        } else {
            return false;
        }
    }



}
