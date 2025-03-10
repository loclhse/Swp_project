package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "News")
public class News {
    @Id
    private  UUID newsId;
    private String title;
    private String description;
    private String source;
    private String category;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

        // Constructor
        public News(String title, String description, String source, String category, LocalDateTime createdAt) {
            this.title = title;
            this.description = description;
            this.source = source;
            this.category = category;
            this.createdAt = createdAt;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getSource() {
            return source;
        }

        public String getCategory() {
            return category;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        // Setters
        public void setTitle(String title) {
            this.title = title;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        // toString() method for easy debugging

    public UUID getNewsId() {
        return newsId;
    }

    public void setNewsId(UUID newsId) {
        this.newsId = newsId;
    }

    @Override
    public String toString() {
        return "News{" +
                "newsId=" + newsId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}




