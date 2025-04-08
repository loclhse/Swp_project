package com.example.Swp_Project.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.UUID;
@Document(collection="injection_history")
public class InjectionHistory {
    @Id
    private UUID id;
    private UUID userId;
    private String childrenName;
    private UUID childrenId;
    private UUID vaccineDetailsId;
    private String doseName;
    private Integer doseNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate injectionDate;
    private UUID appointmentId;

    public InjectionHistory() {
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getDoseName() {
        return doseName;
    }

    public void setDoseName(String doseName) {
        this.doseName = doseName;
    }

    public UUID getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(UUID appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Integer getDoseNumber() {
        return doseNumber;
    }

    public void setDoseNumber(Integer doseNumber) {
        this.doseNumber = doseNumber;
    }

    public LocalDate getInjectionDate() {
        return injectionDate;
    }

    public void setInjectionDate(LocalDate injectionDate) {
        this.injectionDate = injectionDate;
    }

    public UUID getVaccineDetailsId() {
        return vaccineDetailsId;
    }

    public void setVaccineDetailsId(UUID vaccineDetailsId) {
        this.vaccineDetailsId = vaccineDetailsId;
    }

    public UUID getChildrenId() {
        return childrenId;
    }

    public void setChildrenId(UUID childrenId) {
        this.childrenId = childrenId;
    }
}
