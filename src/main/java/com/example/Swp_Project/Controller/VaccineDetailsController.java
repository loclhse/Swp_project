package com.example.Swp_Project.Controller;
import com.example.Swp_Project.DTO.VaccineDetailsDTO;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Service.VaccineDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class VaccineDetailsController {


    @Autowired
    private VaccineDetailService vaccineDetailService;

    @PostMapping("/vaccine-details/{vaccineId}")
    public ResponseEntity<VaccineDetails> createVaccineDetails(
            @PathVariable UUID vaccineId,
            @RequestBody VaccineDetailsDTO details) {
        try {
            VaccineDetails createdDetails = vaccineDetailService.createVaccineDetails(vaccineId, details);
            return ResponseEntity.ok(createdDetails);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null); // 404 if vaccine not found
        }
    }

    @PutMapping("/vaccine-details/{vaccineId}/{detailsId}")
    public ResponseEntity<VaccineDetails> updateVaccineDetails(
            @PathVariable UUID vaccineId,
            @PathVariable UUID detailsId,
            @RequestBody VaccineDetailsDTO updatedDetails) {
        try {
            VaccineDetails updated = vaccineDetailService.updateVaccineDetails(vaccineId, detailsId, updatedDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(null); // 404 if vaccine or details not found
        }
    }

    @DeleteMapping("/vaccine-details/{detailsId}")
    public ResponseEntity<String> deleteVaccineDetails(
            @PathVariable UUID detailsId) {
        try {
            vaccineDetailService.deleteVaccineDetails(detailsId);
            return ResponseEntity.ok("VaccineDetails deleted, fam!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @GetMapping("/vaccinedetails-all")
    public ResponseEntity<List<VaccineDetails>> findAllVaccineDetails() {
        try {
            List<VaccineDetails> details = vaccineDetailService.findAllVaccineDetails();
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/vaccinesdetails-get/{vaccineId}")
    public ResponseEntity<List<VaccineDetails>> getAllVaccineDetailsByVaccineId(
            @PathVariable UUID vaccineId) {
        try {
            List<VaccineDetails> vaccineDetails = vaccineDetailService.getAllVaccineDetailsByVaccineId(vaccineId);

            if (vaccineDetails.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(vaccineDetails, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/vaccinedetails-getById")
    public ResponseEntity<VaccineDetails> getVaccineDetailById(@RequestParam UUID vaccineDetailsId) {
        return vaccineDetailService.findVaccinesDetailById(vaccineDetailsId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}





