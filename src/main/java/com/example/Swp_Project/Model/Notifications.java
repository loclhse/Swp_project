package com.example.Swp_Project.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;
import java.util.UUID;
@Document(collection = "Notifications")

public class Notifications {
    @Id
    private UUID notificationId; // Unique ID (Primary Key)
    private UUID userID;
    private String title;
    private String messages;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public Notifications() {
    }

    public UUID getNotificationId() {
        return notificationId;
    }

    public UUID getUserID() {
        return userID;
    }

    public String getMessages() {
        return messages;
    }

    public void setMessages(String messages) {
        this.messages = messages;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

