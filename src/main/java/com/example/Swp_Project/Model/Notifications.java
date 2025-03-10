package com.example.Swp_Project.Model;



import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.UUID;
@Document(collection = "Notifications")
@AllArgsConstructor
@NoArgsConstructor
public class Notifications {
    @Id
    private UUID notificationId; // Unique ID (Primary Key)
    private UUID userID;
    private String messages;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


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

    // Setters
    public void setNotificationId(UUID notificationId) {
        this.notificationId = notificationId;
    }



    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "notificationId=" + notificationId +
                ", userID='" + userID + '\'' +
                ", messages='" + messages + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

