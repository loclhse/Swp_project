package com.example.Swp_Project.Service;

import com.example.Swp_Project.Dto.vaccineDetailsDto;
import com.example.Swp_Project.Model.Vaccin;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Repositories.vaccineDetailsRepositories;
import com.example.Swp_Project.Repositories.vaccineRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class vaccineDetailService {
    @Autowired
    private vaccineDetailsRepositories vaccineDetailsRepositories;
    @Autowired
    private vaccineRepositories vaccineRepositories ;


    public List<VaccineDetails>findAllVaccineDetails(){

        return vaccineDetailsRepositories.findAll();
    }


    public VaccineDetails updateVaccineDetails(UUID vaccineId, UUID detailsId, vaccineDetailsDto updatedDetails) {
           Vaccin vaccin = vaccineRepositories.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("Vaccine not found for ID: " + vaccineId + ", fam!"));

           VaccineDetails existing = vaccin.getVaccineDetailsList().stream()
                .filter(detail -> detail.getVaccineDetailsId().equals(detailsId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("VaccineDetails not found with ID: " + detailsId + ", dawg!"));

           existing.setDoseRequire(updatedDetails.getDoseRequire());
           existing.setDoseName(updatedDetails.getDoseName());
           existing.setManufacturer(updatedDetails.getManufacturer());
           existing.setQuantity(updatedDetails.getQuantity());
           existing.setStatus(updatedDetails.getStatus());
           existing.setDateBetweenDoses(updatedDetails.getDateBetweenDoses());
           existing.setPrice(updatedDetails.getPrice());
           existing.setUpdateAt(LocalDateTime.now());
           vaccineRepositories.save(vaccin);
           vaccineDetailsRepositories.save(existing);
           return existing;
    }

    public void deleteVaccineDetails(UUID vaccineId, UUID detailsId) {

        Vaccin vaccin = vaccineRepositories.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("Vaccine not found for ID: " + vaccineId + ", fam!"));

        List<VaccineDetails> detailsList = vaccin.getVaccineDetailsList();

        boolean removed = detailsList.removeIf(details -> details.getVaccineDetailsId().equals(detailsId));

        if (!removed) {
            throw new RuntimeException("VaccineDetails not found with ID: " + detailsId + ", bro!");
        }

        vaccineRepositories.save(vaccin);
    }

    public VaccineDetails createVaccineDetails(UUID vaccineId, vaccineDetailsDto details) {

        Vaccin vaccin = vaccineRepositories.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("No vaccine found for ID: " + vaccineId + ", fam!"));
        List<VaccineDetails> detailsList = vaccin.getVaccineDetailsList();
       VaccineDetails vaccineDetails=new VaccineDetails();
       vaccineDetails.setVaccineDetailsId(UUID.randomUUID());
       vaccineDetails.setVaccineId(vaccineId);
       vaccineDetails.setDoseRequire(details.getDoseRequire());
       vaccineDetails.setDoseName(details.getDoseName());
       vaccineDetails.setImageUrl(details.getImageUrl());
       vaccineDetails.setManufacturer(details.getManufacturer());
       vaccineDetails.setQuantity(details.getQuantity());
       vaccineDetails.setStatus(details.getStatus());
       vaccineDetails.setDateBetweenDoses(details.getDateBetweenDoses());
       vaccineDetails.setPrice(details.getPrice());
       vaccineDetails.setCreatedAt(LocalDateTime.now());
       detailsList.add(vaccineDetails);
       vaccineRepositories.save(vaccin);
       vaccineDetailsRepositories.save(vaccineDetails);

       return vaccineDetails;
    }

}

