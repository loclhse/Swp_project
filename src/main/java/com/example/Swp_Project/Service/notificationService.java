package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Notifications;
import com.example.Swp_Project.Repositories.notificationsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class notificationService {
    @Autowired
    private notificationsRepositories notificationRepository;




    public Notifications createNotification(Notifications notifications) {
        Notifications notification = new Notifications();
        notification.setNotificationId(UUID.randomUUID());
        notification.setUserID(notifications.getUserID());
        notification.setMessages(notifications.getMessages());
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }


    public List<Notifications> getAllNotificationsSorted() {
        return notificationRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Notifications> getNotificationById(UUID notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public ResponseEntity<?> updateNotification(UUID id,Notifications noti) {
    Optional<Notifications> notifications = notificationRepository.findById(id);
    if (notifications.isPresent()) {
        Notifications existnotifications = new Notifications();

        existnotifications.setMessages(noti.getMessages());
        existnotifications.setCreatedAt(LocalDateTime.now());
        Notifications notii= notificationRepository.save(existnotifications);
        return ResponseEntity.ok(notii);
    }
       return ResponseEntity.status(500).body("not found notifications");

}

     public ResponseEntity<?> deleteNotification(UUID id) {
        if (!notificationRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Notification not found.");
        }

        notificationRepository.deleteById(id);
        return ResponseEntity.ok("Notification deleted successfully.");
    }

    public List<Notifications>findNotificationOfUser(UUID userid){
       return notificationRepository.findByUserID(userid);

    }

        }


