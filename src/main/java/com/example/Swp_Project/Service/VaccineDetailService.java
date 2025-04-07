package com.example.Swp_Project.Service;

import com.example.Swp_Project.Controller.InjectionHistoryController;
import com.example.Swp_Project.DTO.VaccineDetailsDTO;
import com.example.Swp_Project.Model.Vaccin;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Repositories.VaccineDetailsRepositories;
import com.example.Swp_Project.Repositories.VaccineRepositories;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VaccineDetailService {
    @Autowired
    private VaccineDetailsRepositories vaccineDetailsRepositories;
    @Autowired
    private VaccineRepositories vaccineRepositories ;

    private static final Logger logger = LoggerFactory.getLogger(InjectionHistoryController.class);


    public Optional<VaccineDetails>findVaccinesDetailById(UUID vaccinedetailid){

        return vaccineDetailsRepositories.findById(vaccinedetailid);

    }

    public List<VaccineDetails>findAllVaccineDetails(){

        return vaccineDetailsRepositories.findAll();
    }


    public VaccineDetails updateVaccineDetails(UUID vaccineId, UUID detailsId, VaccineDetailsDTO updatedDetails) {
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
           existing.setDosageAmount(updatedDetails.getDosageAmount());
           existing.setAgeRequired(updatedDetails.getAgeRequired());
           existing.setBoosterInterval(updatedDetails.getBoosterInterval());
           existing.setUpdateAt(LocalDateTime.now());
           vaccineRepositories.save(vaccin);
           vaccineDetailsRepositories.save(existing);
           return existing;
    }

    public void deleteVaccineDetails(UUID detailsId) {
        if (!vaccineDetailsRepositories.existsById(detailsId)) {
            throw new IllegalArgumentException("Vaccine detail with ID " + detailsId + " does not exist.");
        }

        vaccineDetailsRepositories.deleteById(detailsId);
        logger.info("Deleted VaccineDetail with ID: {}", detailsId);

    }

    public VaccineDetails createVaccineDetails(UUID vaccineId, VaccineDetailsDTO details) {

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
       vaccineDetails.setStatus("In Stock");
       vaccineDetails.setDateBetweenDoses(details.getDateBetweenDoses());
       vaccineDetails.setPrice(details.getPrice());
       vaccineDetails.setDosageAmount(details.getDosageAmount());
       vaccineDetails.setAgeRequired(details.getAgeRequired());
       vaccineDetails.setBoosterInterval(details.getBoosterInterval());
       vaccineDetails.setCreatedAt(LocalDateTime.now());
       detailsList.add(vaccineDetails);
       vaccineRepositories.save(vaccin);
       vaccineDetailsRepositories.save(vaccineDetails);

       return vaccineDetails;
    }

    public List<VaccineDetails>getAllVaccineDetailsByVaccineId(UUID vaccineId){

        return vaccineDetailsRepositories.findByVaccineId(vaccineId);

    }

}

