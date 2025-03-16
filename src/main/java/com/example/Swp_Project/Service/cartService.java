package com.example.Swp_Project.Service;

import com.example.Swp_Project.Dto.appointmentDto;
import com.example.Swp_Project.Dto.cartDisplayDto;
import com.example.Swp_Project.Model.Appointment;
import com.example.Swp_Project.Model.CartItem;
import com.example.Swp_Project.Model.User;
import com.example.Swp_Project.Model.VaccineDetails;
import com.example.Swp_Project.Repositories.appointmentRepositories;
import com.example.Swp_Project.Repositories.userRepositories;
import com.example.Swp_Project.Repositories.vaccineDetailsRepositories;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class cartService {
    @Autowired
    private userRepositories userRepo;
    @Autowired
    private vaccineDetailsRepositories vaccineDetailsRepository;
    @Autowired
    private appointmentRepositories appointmentRepositories;

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;


    private final Map<UUID, List<CartItem>> tempCart = new HashMap<>();
    private final Map<UUID, appointmentDto> tempAppointments = new HashMap<>();

    @PostConstruct
    public void logConfig() {
        System.out.println("CartService Startup - Checking @Value:");
        System.out.println("vnp_TmnCode: " + vnp_TmnCode);
        System.out.println("vnp_HashSecret: " + vnp_HashSecret);
        System.out.println("vnp_Url: " + vnp_Url);
        System.out.println("vnp_ReturnUrl: " + vnp_ReturnUrl);
    }

    public String addToCart(UUID vaccineDetailsId, Integer quantity, UUID userId) throws Exception {
        Optional<VaccineDetails> vaccinedetailOpt = vaccineDetailsRepository.findById(vaccineDetailsId);
        if (vaccinedetailOpt.isEmpty()) {
            throw new Exception("Vaccine not found");
        }

        VaccineDetails vaccinedetail = vaccinedetailOpt.get();
        if (vaccinedetail.getQuantity() < quantity) {
            throw new Exception("Insufficient stock");
        }

        CartItem cartItem = new CartItem();
        cartItem.setUserId(userId);
        cartItem.setVaccineDetailsId(vaccineDetailsId);
        cartItem.setQuantity(quantity);
        tempCart.computeIfAbsent(userId, k -> new ArrayList<>()).add(cartItem);

        return "Added to cart successfully";
    }



    public List<cartDisplayDto> getTempCart(UUID userId) throws Exception {
        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        if (cartItems.isEmpty()) {
            throw new Exception("Cart is empty");
        }

        List<cartDisplayDto> cartDetails = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
            Optional<VaccineDetails> vaccinedetailsOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());

            if (vaccinedetailsOpt.isEmpty()) {
                throw new Exception("Vaccine not found for ID " + cartItem.getVaccineDetailsId());
            }

            VaccineDetails vaccine = vaccinedetailsOpt.get();

            cartDetails.add(new cartDisplayDto(
                    vaccine.getDoseName(),
                    vaccine.getPrice(),
                    vaccine.getQuantity()
            ));
        }
            return cartDetails;
    }


    public String initiateCheckout(UUID userId, appointmentDto appointmentDTO) throws Exception {

        System.out.println("InitiateCheckout - Verifying @Value:");
        System.out.println("vnp_TmnCode: " + vnp_TmnCode);
        System.out.println("vnp_HashSecret: " + vnp_HashSecret);
        System.out.println("vnp_Url: " + vnp_Url);
        System.out.println("vnp_ReturnUrl: " + vnp_ReturnUrl);

        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        if (cartItems.isEmpty()) {
            throw new Exception("Cart is empty");
        }

        double total = 0.0;
        for (CartItem cartItem : cartItems) {
            Optional<VaccineDetails> vaccinedetailsOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            if (vaccinedetailsOpt.isEmpty()) {
                throw new Exception("there is no vaccine in cart");
            }
            VaccineDetails vaccinedetail = vaccinedetailsOpt.get();
            if (vaccinedetail.getQuantity() < cartItem.getQuantity()) {
                throw new Exception("Outstock for " + vaccinedetail.getDoseName());
            }

            total += vaccinedetail.getPrice() * cartItem.getQuantity();
        }

        tempAppointments.put(userId, appointmentDTO);

        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf((int) (total * 100)));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", UUID.randomUUID().toString().substring(0, 8));
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang cho user " + userId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        vnp_Params.put("vnp_CreateDate", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        System.out.println("InitiateCheckout - vnp_Params: " + vnp_Params);
        String hashData = String.join("&", vnp_Params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.toList()));
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        String paymentUrl = vnp_Url + "?" + hashData + "&vnp_SecureHash=" + vnp_SecureHash;
        System.out.println("InitiateCheckout - VNPAY Payment URL: " + paymentUrl);
        return paymentUrl;
    }

    public String processReturn(HttpServletRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        System.out.println("ProcessReturn - All Request Params:");
        if (parameterMap.isEmpty()) {
            System.out.println("  (No parameters found)");
        } else {
            parameterMap.forEach((key, value) ->
                    System.out.println("  " + key + "=" + String.join(",", value)));
        }

        for (String key : parameterMap.keySet()) {
            if (key != null && key.startsWith("vnp_")) {
                String value = request.getParameter(key);
                params.put(key, value);
                System.out.println("ProcessReturn - Added to params: " + key + "=" + value);
            }
        }

        String vnp_SecureHash = params.remove("vnp_SecureHash");
        System.out.println("ProcessReturn - Parameters before hash generation:");
        params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> System.out.println("  " + e.getKey() + "=" + e.getValue()));

        String hashData = String.join("&", params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.toList()));

        System.out.println("Hash Data before hash: [" + hashData + "]");
        System.out.println("ProcessReturn - Hash Data: " + hashData);
        System.out.println("ProcessReturn - vnp_HashSecret: [" + vnp_HashSecret + "]"); // Log with brackets
        System.out.println("ProcessReturn - vnp_SecureHash: " + vnp_SecureHash);
        String calculatedHash = hmacSHA512(vnp_HashSecret, hashData);
        System.out.println("ProcessReturn - Calculated Hash: " + calculatedHash);

        if (vnp_SecureHash == null) {
            throw new Exception("Missing secure hash");
        }

        // Compare hashes case-insensitively
        if (!vnp_SecureHash.equalsIgnoreCase(calculatedHash)) {
            System.out.println("Hash comparison failed:");
            System.out.println("Expected: " + vnp_SecureHash.toLowerCase());
            System.out.println("Actual  : " + calculatedHash.toLowerCase());
            throw new Exception("Invalid checksum");
        }

        if ("00".equals(params.get("vnp_ResponseCode"))) {
            UUID userId = UUID.fromString(params.get("vnp_OrderInfo").split("user ")[1]);
            List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
            if (cartItems.isEmpty()) {
                throw new Exception("Cart is empty on return");
            }

            appointmentDto appointmentDTO = tempAppointments.get(userId);
            if (appointmentDTO == null) {
                throw new Exception("Appointment data not found");
            }

            Appointment appointment = new Appointment();
            appointment.setUserId(userId);
            appointment.setAppointmentId(UUID.randomUUID());
            appointment.setChildrenName(appointmentDTO.getChildrenName());
            appointment.setNote(appointmentDTO.getNote());
            appointment.setMedicalIssue(appointmentDTO.getMedicalIssue());
            appointment.setChildrenGender(appointmentDTO.getChildrenGender());
            appointment.setDateOfBirth(appointmentDTO.getDateOfBirth());
            appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
            appointment.setTimeStart(appointmentDTO.getTimeStart());
            appointment.setStatus("Pending");
            appointment.setCreateAt(LocalDateTime.now());

            List<VaccineDetails> vaccineDetailsList = new ArrayList<>();
            for (CartItem cartItem : cartItems) {
                Optional<VaccineDetails> vaccineOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
                if (vaccineOpt.isEmpty()) {
                    throw new Exception("Vaccine not found during processing");
                }
                VaccineDetails vaccine = vaccineOpt.get();
                vaccine.setQuantity(vaccine.getQuantity() - cartItem.getQuantity());
                vaccineDetailsRepository.save(vaccine);
                vaccineDetailsList.add(vaccine);
            }
            appointment.setVaccineDetailsList(vaccineDetailsList);

            appointmentRepositories.save(appointment);
            tempCart.remove(userId);
            tempAppointments.remove(userId);

            return "Payment and appointment creation successful";
        } else {
            throw new Exception("Payment failed: " + params.get("vnp_ResponseCode"));
        }
    }

    private String hmacSHA512(String key, String data) throws Exception {
        System.out.println("hmacSHA512 - Input data: [" + data + "]");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hmac) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff)); // Ensure unsigned byte
        }
        return sb.toString();
    }
}
