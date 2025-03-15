package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Dto.appointmentDto;
import com.example.Swp_Project.Dto.cartDisplayDto;
import com.example.Swp_Project.Model.CartItem;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Service.cartService;
import com.example.Swp_Project.Service.vaccineDetailService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class cartController {

    @Autowired
    private cartService cartService;
    @Autowired
    private vaccineDetailService vaccineDetailService;

    @PostMapping("/cart/add/{vaccineDetailsId}/{quantity}/{userId}")
    public ResponseEntity<String> addToCart(
            @PathVariable UUID vaccineDetailsId,
            @PathVariable Integer quantity,
            @PathVariable UUID userId) {
        try {
            String result = cartService.addToCart(vaccineDetailsId, quantity, userId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<String> checkout(
            @RequestParam UUID userId,
            @RequestBody appointmentDto appointmentDTO) {
        try {
            String paymentUrl = cartService.initiateCheckout(userId, appointmentDTO);
            return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cart/return")
    public ResponseEntity<String> handleReturn(HttpServletRequest request) {
        try {
            // Log all params
            System.out.println("VNPay Callback Params:");
            request.getParameterMap().forEach((key, value) ->
                    System.out.println(key + ": " + String.join(",", value))
            );

            String result = cartService.processReturn(request);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
