package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.Notifications;
import com.example.Swp_Project.Service.notificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequestMapping("/api/notifications")
@RestController
public class notificationsController {
    @Autowired
    private notificationService notificationService;


    @PostMapping
    public ResponseEntity<Notifications> createNotification(@RequestBody Notifications notifications) {
        Notifications notification = notificationService.createNotification(notifications);
        return ResponseEntity.ok(notification);
    }


    @GetMapping
    public ResponseEntity<List<Notifications>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotificationsSorted());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Notifications> getNotificationById(@PathVariable UUID id) {
        Optional<Notifications> notification = notificationService.getNotificationById(id);
        return notification.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?>updateNotifications(@PathVariable UUID id,@RequestBody Notifications noti){
        return notificationService.updateNotification(id,noti);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

}




