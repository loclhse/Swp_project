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
import jakarta.servlet.http.HttpServletRequest;
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

    private final Map<UUID, List<CartItem>> tempCart = new HashMap<>();
    private final Map<UUID, appointmentDto> tempAppointments = new HashMap<>();

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl = "https://vaccinemanagement-2f854cbfd074.herokuapp.com/api/cart/return";

        public String addToCart(UUID vaccineDetailsId, Integer quantity, UUID userId) throws Exception {
            Optional<VaccineDetails> vaccineOpt = vaccineDetailsRepository.findById(vaccineDetailsId);
            if (vaccineOpt.isEmpty()) {
                throw new Exception("Vaccine not found");
            }

            VaccineDetails vaccine = vaccineOpt.get();
            if (vaccine.getQuantity() < quantity) {
                throw new Exception("Insufficient stock");
            }

            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setVaccineDetailsId(vaccineDetailsId);
            cartItem.setQuantity(quantity);
            tempCart.computeIfAbsent(userId, k -> new ArrayList<>()).add(cartItem);

            return "Added to cart successfully";
        }

    public List<cartDisplayDto> getCartDetails(UUID userId) throws Exception {
        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        if (cartItems.isEmpty()) {
            throw new Exception("Cart is empty");
        }

        List<cartDisplayDto> cartDetails = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            Optional<VaccineDetails> vaccineOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            if (vaccineOpt.isEmpty()) {
                throw new Exception("Vaccine not found in cart");
            }
            VaccineDetails vaccineDetails = vaccineOpt.get();
            cartDetails.add(new cartDisplayDto(vaccineDetails.getDoseName(), vaccineDetails.getPrice(), cartItem.getQuantity()));
        }
        return cartDetails;
    }

    public String initiateCheckout(UUID userId, appointmentDto appointmentDTO) throws Exception {
        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        if (cartItems.isEmpty()) {
            throw new Exception("Cart is empty");
        }

        double total = 0.0;
        for (CartItem cartItem : cartItems) {
            Optional<VaccineDetails> vaccineOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            if (vaccineOpt.isEmpty()) {
                throw new Exception("Vaccine not found in cart");
            }
            VaccineDetails vaccine = vaccineOpt.get();
            if (vaccine.getQuantity() < cartItem.getQuantity()) {
                throw new Exception("Insufficient stock for " + vaccine.getDoseName());
            }
            total += vaccine.getPrice() * cartItem.getQuantity();
        }

        // Store the AppointmentDTO separately
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

        String hashData = String.join("&", vnp_Params.entrySet().stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.toList()));
        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        return vnp_Url + "?" + hashData + "&vnp_SecureHash=" + vnp_SecureHash;
    }

    public String processReturn(HttpServletRequest request) throws Exception {
        Map<String, String> params = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            if (key.startsWith("vnp_")) { // Only vnp_ params
                params.put(key, request.getParameter(key));
            }
        }

        String vnp_SecureHash = params.remove("vnp_SecureHash");
        String hashData = String.join("&", params.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue()) // Raw values
                .collect(Collectors.toList()));

        System.out.println("Hash Data: " + hashData);
        System.out.println("vnp_SecureHash: " + vnp_SecureHash);
        String calculatedHash = hmacSHA512(vnp_HashSecret, hashData);
        System.out.println("Calculated Hash: " + calculatedHash);

        if (!calculatedHash.equals(vnp_SecureHash)) {
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
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmac);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
