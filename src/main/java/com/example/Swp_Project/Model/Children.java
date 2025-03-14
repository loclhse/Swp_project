package com.example.Swp_Project.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.aggregation.ArrayOperators;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Document(collection = "Children")
public class Children {
    @Id
    private UUID childrenId;
    private String childrenName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String gender;
    private String medicalIssue;
    private UUID userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creatAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateAt;

    public Children() {
    }

    public String getMedicalIssue() {
        return medicalIssue;
    }

    public void setMedicalIssue(String medicalIssue) {
        this.medicalIssue = medicalIssue;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getChildrenName() {
        return childrenName;
    }

    public void setChildrenName(String childrenName) {
        this.childrenName = childrenName;
    }

    public UUID getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(UUID childrenId) {
        this.childrenId = childrenId;
    }

    public LocalDateTime getCreatAt() {
        return creatAt;
    }

    public void setCreatAt(LocalDateTime creatAt) {
        this.creatAt = creatAt;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }





}
