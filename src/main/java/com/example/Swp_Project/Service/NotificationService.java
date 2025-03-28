package com.example.Swp_Project.Service;

import com.example.Swp_Project.Model.Notifications;
import com.example.Swp_Project.Repositories.NotificationsRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {
    @Autowired
    private NotificationsRepositories notificationRepository;

    public List<Notifications>findAllNotifications(){
        return notificationRepository.findAll();
    }

    public Optional<Notifications> getNotificationById(UUID notificationId) {
        return notificationRepository.findById(notificationId);
    }

    public void deleteNotification(UUID userId) {
        List<Notifications> notifications = notificationRepository.findByUserID(userId);
        if (notifications.isEmpty()) {
            throw new NotFoundException("No notifications found for user with ID: " + userId);
        }
        notificationRepository.deleteAll(notifications);
    }

    public List<Notifications>findNotificationOfUser(UUID userid){
       return notificationRepository.findByUserID(userid);
    }

        }


