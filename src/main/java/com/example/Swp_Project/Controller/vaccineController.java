package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.vaccineDto;
import com.example.Swp_Project.Model.Vaccin;
import com.example.Swp_Project.Service.vaccineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api")
public class vaccineController {

    @Autowired
    private vaccineService vaccineService;

    @PostMapping("/vaccines-create")
    public ResponseEntity<Vaccin> createVaccine(@RequestBody vaccineDto vaccine) {
        return ResponseEntity.ok(vaccineService.createVaccine(vaccine));
    }

    @GetMapping("/vaccines-all")
    public ResponseEntity<List<Vaccin>> getAllVaccines() {
        return ResponseEntity.ok(vaccineService.getAllVaccines());
    }

    @GetMapping("/vaccines-get/{vaccineId}")
    public ResponseEntity<Vaccin> getVaccineById(@PathVariable UUID vaccineId) {
        return ResponseEntity.ok(vaccineService.getVaccineById(vaccineId));
    }

    @DeleteMapping("/vaccines-delete/{vaccineId}")
    public ResponseEntity<String> deleteVaccine(@PathVariable UUID vaccineId) {
        return ResponseEntity.ok(vaccineService.deleteVaccine(vaccineId));
    }



}



