package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.vaccineDetailsDto;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Service.vaccineDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vaccineDetails")
public class vaccineDetailsController {
    @Autowired
    private vaccineDetailService vaccineDetailService;

    @PostMapping("/{vaccineId}/vaccine")
    public ResponseEntity<VaccineDetails> createVaccineDetails(
            @PathVariable UUID vaccineId,
            @RequestBody vaccineDetailsDto details) {
        try {
            VaccineDetails createdDetails = vaccineDetailService.createVaccineDetails(vaccineId, details);
            return ResponseEntity.ok(createdDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null); // 404 if vaccine not found
        }
    }

    @PutMapping("/{detailsId}/vaccines/{vaccineId}")
    public ResponseEntity<VaccineDetails> updateVaccineDetails(
            @PathVariable UUID vaccineId,
            @PathVariable UUID detailsId,
            @RequestBody vaccineDetailsDto updatedDetails) {
        try {
            VaccineDetails updated = vaccineDetailService.updateVaccineDetails(vaccineId, detailsId, updatedDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null); // 404 if vaccine or details not found
        }
    }

    @DeleteMapping("/vaccines/{vaccineId}/details/{detailsId}")
    public ResponseEntity<String> deleteVaccineDetails(
            @PathVariable UUID vaccineId,
            @PathVariable UUID detailsId) {
        try {
            vaccineDetailService.deleteVaccineDetails(vaccineId, detailsId);
            return ResponseEntity.ok("VaccineDetails deleted, fam!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/vaccine-details")
    public ResponseEntity<List<VaccineDetails>> findAllVaccineDetails() {
        try {
            List<VaccineDetails> details = vaccineDetailService.findAllVaccineDetails();
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}





