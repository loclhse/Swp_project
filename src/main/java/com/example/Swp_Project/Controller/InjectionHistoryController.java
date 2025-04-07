package com.example.Swp_Project.Controller;

import com.example.Swp_Project.Model.InjectionHistory;
import com.example.Swp_Project.Service.CartService;
import com.example.Swp_Project.Service.InjectionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class InjectionHistoryController {

    @Autowired
    private InjectionHistoryService injectionHistoryService;

    private static final Logger logger = LoggerFactory.getLogger(InjectionHistoryController.class);

    @GetMapping("/injection-history/{childrenId}")
    public ResponseEntity<Map<String, Object>> findByChildrenIdDesc(@PathVariable UUID childrenId) {
        try {
            List<InjectionHistory> histories = injectionHistoryService.findByChildrenIdDesc(childrenId);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully retrieved injection history for childrenId: " + childrenId);
            successResponse.put("data", histories);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InvalidRequest");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            logger.error("Not found: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ResourceNotFound");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

        @GetMapping("/injection-history/{vaccineDetailsId}")
        public ResponseEntity<Map<String, Object>> findByVaccineDetailsId(@PathVariable UUID vaccineDetailsId) {
            try {
                List<InjectionHistory> histories = injectionHistoryService.findByVaccineDetailsId(vaccineDetailsId);
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("message", "Successfully retrieved injection history for vaccineDetailsId: " + vaccineDetailsId);
                successResponse.put("data", histories);
                return ResponseEntity.ok(successResponse);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid request: {}", e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "InvalidRequest");
                errorResponse.put("message", e.getMessage());
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            } catch (NullPointerException e) {
                logger.error("Not found: {}", e.getMessage());
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "ResourceNotFound");
                errorResponse.put("message", e.getMessage());
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                logger.error("Internal server error: {}", e.getMessage(), e);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "InternalServerError");
                errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
                return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    @DeleteMapping("/injection-history/{childrenId}")
    public ResponseEntity<Map<String, Object>> deleteByChildrenId(@PathVariable UUID childrenId) {
        try {
            injectionHistoryService.deleteByChildrenId(childrenId);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully deleted injection history for childrenId: " + childrenId);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InvalidRequest");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            logger.error("Not found: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ResourceNotFound");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/injection-history")
    public ResponseEntity<Map<String, Object>> getAll() {
        try {
            List<InjectionHistory> histories = injectionHistoryService.getAll();
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully retrieved all injection history records");
            successResponse.put("data", histories);
            return ResponseEntity.ok(successResponse);
        } catch (NullPointerException e) {
            logger.error("Not found: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ResourceNotFound");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/injection-history/{userId}")
    public ResponseEntity<Map<String, Object>> findByUserId(@PathVariable UUID userId) {
        try {
            List<InjectionHistory> histories = injectionHistoryService.findByUserId(userId);
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "Successfully retrieved injection history for userId: " + userId);
            successResponse.put("data", histories);
            return ResponseEntity.ok(successResponse);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InvalidRequest");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            logger.error("Not found: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "ResourceNotFound");
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal server error: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "InternalServerError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    }


