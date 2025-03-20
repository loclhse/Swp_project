package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Notifications;
import com.example.Swp_Project.Service.notificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.*;

@RequestMapping("/api")
@RestController
public class notificationsController {

    @Autowired
    private notificationService notificationService;


    @PostMapping("/notification-create")
    public ResponseEntity<Notifications> createNotification(@RequestBody Notifications notifications) {
        Notifications notification = notificationService.createNotification(notifications);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/notification-all")
    public ResponseEntity<List<Notifications>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotificationsSorted());
    }

    @GetMapping("/notification-get/{id}")
    public ResponseEntity<Notifications> getNotificationById(@PathVariable UUID id) {
        Optional<Notifications> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/notification-update/{id}")
    public ResponseEntity<?>updateNotifications(@PathVariable UUID id,@RequestBody Notifications noti){
        return notificationService.updateNotification(id,noti);
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
        } catch (NotFoundException e) {

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

}




