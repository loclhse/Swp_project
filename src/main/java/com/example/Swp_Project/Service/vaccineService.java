package com.example.Swp_Project.Service;

import com.example.Swp_Project.DTO.vaccineDto;
import com.example.Swp_Project.Model.Vaccin;

import com.example.Swp_Project.Repositories.vaccineRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.UUID;


@Service
public class vaccineService {
    @Autowired
    private vaccineRepositories vaccineRepositories;

    public Vaccin createVaccine(vaccineDto vaccine) {
        Vaccin vaccin=new Vaccin();
        vaccin.setVaccineId(UUID.randomUUID());
        vaccin.setIllnessName(vaccine.getIllnessName());
        vaccin.setAgeLimit(vaccine.getAgeLimit());
        vaccin.setDescriptions(vaccine.getDescriptions());
        return vaccineRepositories.save(vaccin);
    }

    public List<Vaccin> getAllVaccines() {
        return vaccineRepositories.findAll();
    }

    public Vaccin getVaccineById(UUID vaccineId) {
        return vaccineRepositories.findById(vaccineId)
                .orElseThrow(() -> new RuntimeException("Vaccine not found for ID: " + vaccineId + ", fam!"));
    }

    public String deleteVaccine(UUID id) {
        try {
            vaccineRepositories.deleteById(id);
            return "Deleted successfully";
        } catch (EmptyResultDataAccessException e) {
            return "There is some error in the deleting process: Vaccine not found";
        } catch (Exception e) {
            return "There is some error in the deleting process";
        }
    }


}