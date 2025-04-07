package com.example.Swp_Project.Controller;

import com.example.Swp_Project.DTO.AppointmentDTO;
import com.example.Swp_Project.DTO.CartDisplayDTO;
import com.example.Swp_Project.Service.CartService;
import com.example.Swp_Project.Service.VaccineDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private VaccineDetailService vaccineDetailService;

    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    @DeleteMapping("/cart/remove/{userId}/{vaccineDetailsId}")
    public ResponseEntity<String> deleteFromCart(
            @PathVariable UUID userId,
            @PathVariable UUID vaccineDetailsId) throws Exception {
        String result = cartService.deleteFromCart(vaccineDetailsId, userId);
        return ResponseEntity.ok(result);
    }

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
    public ResponseEntity<List<CartDisplayDTO>> checkCart(@PathVariable UUID userId) {
        try {
            List<CartDisplayDTO> cartItems = cartService.getCart(userId);
            System.out.println("CheckCart - Cart Items: " + cartItems);
            return new ResponseEntity<>(cartItems, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("CheckCart - Error: " + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cart/checkout")
    public ResponseEntity<Map<String,Object>> checkout(
            @RequestParam UUID userId,
            @RequestBody AppointmentDTO appointmentdto) throws Exception {
        try {
            String paymentUrl = cartService.initiateCheckout(userId, appointmentdto);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", paymentUrl);
            return ResponseEntity.ok(successResponse);
        } catch (CartService.CartEmptyException e) {
            Map<String, Object> errorResponse = new HashMap<>();

            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (CartService.MissingDataException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "MissingData");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (CartService.ResourceNotFoundException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ResourceNotFound");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (CartService.OutOfStockException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "OutOfStock");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (CartService.InvalidDosageException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InvalidDosage");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/cart/return")
    public void handleReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
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
            response.sendRedirect("https://steady-duckanoo-a4608b.netlify.app/paymentsuccess?status=success&message=" + URLEncoder.encode(result, StandardCharsets.UTF_8));

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HandleReturn - Error: " + e.getMessage());
            response.sendRedirect("https://steady-duckanoo-a4608b.netlify.app/paymentfailure?status=failed&message=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        }
    }

    @PostMapping("/cart/checkout/cash/{userId}")
    public ResponseEntity<Map<String, Object>> initiateCashCheckout(
            @PathVariable UUID userId,
            @RequestBody AppointmentDTO appointmentDTO) {
        try{
        String result = cartService.initiateCashCheckout(userId, appointmentDTO);
        Map<String, Object> successResponse = new HashMap<>();
        successResponse.put("message", result);
        return ResponseEntity.ok(successResponse);
    } catch (CartService.CartEmptyException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "CartEmpty");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (CartService.MissingDataException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "MissingData");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (CartService.ResourceNotFoundException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "ResourceNotFound");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    } catch (CartService.OutOfStockException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "OutOfStock");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (CartService.InvalidDosageException e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "InvalidDosage");
        errorResponse.put("message", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "InternalServerError");
        errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
    }



