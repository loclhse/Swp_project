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
import java.util.Map;
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
            System.out.println("AddToCart - vaccineDetailsId: " + vaccineDetailsId + ", quantity: " + quantity + ", userId: " + userId);
            String result = cartService.addToCart(vaccineDetailsId, quantity, userId);
            System.out.println("AddToCart - Result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("AddToCart - Error: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cart/check/{userId}")
    public ResponseEntity<List<cartDisplayDto>> checkCart(@PathVariable UUID userId) {
        try {
            List<cartDisplayDto> cartItems = cartService.getTempCart(userId);
            System.out.println("CheckCart - Cart Items: " + cartItems);
            return new ResponseEntity<>(cartItems, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CheckCart - Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<String> checkout(
            @RequestParam UUID userId,
            @RequestBody appointmentDto appointmentDTO) {
        try {
            String paymentUrl = cartService.initiateCheckout(userId, appointmentDTO);
            System.out.println("Checkout - Payment URL sent to VNPAY: " + paymentUrl);
            return new ResponseEntity<>(paymentUrl, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Checkout - Error: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/cart/return")
    public ResponseEntity<String> handleReturn(HttpServletRequest request) {
        try {
            System.out.println("VNPAY Callback Received:");
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Request URL: " + request.getRequestURL());
            System.out.println("Query String: " + request.getQueryString());
            System.out.println("VNPay Callback Params:");
            Map<String, String[]> paramMap = request.getParameterMap();
            if (paramMap.isEmpty()) {
                System.out.println("  (No parameters found)");
            } else {
                paramMap.forEach((key, value) ->
                        System.out.println("  " + key + ": " + String.join(",", value)));
            }

            String result = cartService.processReturn(request);
            System.out.println("HandleReturn - Result: " + result);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HandleReturn - Error: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
