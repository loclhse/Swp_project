package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Notifications;
import com.example.Swp_Project.Service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RequestMapping("/api")
@RestController
public class NotificationsController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notifications-all")
    public List<Notifications> getAllNotifications() {
        return notificationService.findAllNotifications();
    }

    @DeleteMapping("/notification-delete/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notifications-getByUserId/{userId}")
    public ResponseEntity<List<Notifications>> getNotificationsByUserId(@PathVariable UUID userId) {
        try {
            List<Notifications> notifications = notificationService.findNotificationOfUser(userId);
            return ResponseEntity.ok(notifications);
        } catch (RuntimeException e) {
            System.out.println("NotFoundException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("X-Error-Message", "User not found for ID: " + userId)
                    .body(Collections.emptyList());
        } catch (Exception e) {
            System.out.println("Error fetching notifications for user " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("X-Error-Message", "Failed to fetch notifications: " + e.getMessage())
                    .body(Collections.emptyList());
        }
    }

    @DeleteMapping("/notifications-deletebyUserId/{userId}")
    public ResponseEntity<String> deleteNotificationsByUserId(@PathVariable UUID userId) {
        try {
            notificationService.deleteNotification(userId);
            return ResponseEntity.ok("Notifications deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete notifications");
        }
    }

}




