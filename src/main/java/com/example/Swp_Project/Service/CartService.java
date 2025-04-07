package com.example.Swp_Project.Service;
import com.example.Swp_Project.DTO.AppointmentDTO;
import com.example.Swp_Project.DTO.CartDisplayDTO;
import com.example.Swp_Project.Model.*;
import com.example.Swp_Project.Repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;

import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired
    private VaccineDetailsRepositories vaccineDetailsRepository;
    @Autowired
    private AppointmentRepositories appointmentRepositories;
    @Autowired
    private PaymentsRepositories paymentsRepositories;
    @Autowired
    private UserRepositories userRepositories;
    @Autowired
    private AppointmentDetailsRepositories appointmentDetailsRepositories;

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    private static final Logger logger = LoggerFactory.getLogger(CartService.class);
    private final Map<UUID, List<CartItem>> tempCart = new HashMap<>();
    private final Map<UUID, AppointmentDTO> tempAppointments = new HashMap<>();

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
        cartItem.setVaccineDetailsId(vaccineDetailsId);
        cartItem.setQuantity(quantity);
        tempCart.computeIfAbsent(userId, k -> new ArrayList<>()).add(cartItem);

        return "Added to cart successfully";
    }

    public String deleteFromCart(UUID vaccineDetailsId, UUID userId) throws Exception {
        List<CartItem> userCart = tempCart.get(userId);
        if (userCart == null || userCart.isEmpty()) {
            throw new Exception("Cart is empty for user: " + userId);
        }

        boolean removed = userCart.removeIf(item -> item.getVaccineDetailsId().equals(vaccineDetailsId));
        if (!removed) {
            throw new Exception("Item with vaccineDetailsId " + vaccineDetailsId + " not found in cart for user: " + userId);
        }

        if (userCart.isEmpty()) {
            tempCart.remove(userId);
        }

        return "Item removed from cart successfully";
    }

    public List<CartDisplayDTO> getCart(UUID userId) throws Exception {
        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        List<CartDisplayDTO> cartItemResponses = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Optional<VaccineDetails> vaccineDetailsOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            if (vaccineDetailsOpt.isEmpty()) {
                throw new Exception("Vaccine not found for ID: " + cartItem.getVaccineDetailsId());
            }
            VaccineDetails vaccineDetails = vaccineDetailsOpt.get();
            CartDisplayDTO response = new CartDisplayDTO(cartItem, vaccineDetails);
            cartItemResponses.add(response);
        }

        return cartItemResponses;
    }


    public String initiateCheckout(UUID userId, AppointmentDTO appointmentDTO)  throws InvalidDosageException,CartEmptyException,MissingDataException,OutOfStockException,ResourceNotFoundException {

        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        if (cartItems.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        if (appointmentDTO == null) {
            logger.error("Appointment data is missing for userId: {}", userId);
            throw new MissingDataException("Appointment data is missing");
        }

        Set<UUID> uniqueVaccineDetailsIds = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            uniqueVaccineDetailsIds.add(cartItem.getVaccineDetailsId());
        }
        if (uniqueVaccineDetailsIds.size() > 2) {
            logger.error("Too many different vaccine doses in the cart: {} (maximum allowed is 2)", uniqueVaccineDetailsIds.size());
            throw new TooManyDoseInCartException("Only two different vaccine doses are allowed per appointment");
        }

        double total = 0.0;
        for (CartItem cartItem : cartItems) {

            Optional<VaccineDetails> vaccinedetailsOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            if (vaccinedetailsOpt.isEmpty()) {
                throw new ResourceNotFoundException("there is no vaccine in cart");
            }

            VaccineDetails vaccinedetail = vaccinedetailsOpt.get();
            if (vaccinedetail.getQuantity() < cartItem.getQuantity()) {
                throw new OutOfStockException("Outstock for " + vaccinedetail.getDoseName());
            }



            if (vaccinedetail.getAgeRequired() != null && appointmentDTO.getDateOfBirth() != null) {

                int childrenAgeInYears = Period.between(appointmentDTO.getDateOfBirth(), LocalDate.now()).getYears();
                if (childrenAgeInYears < vaccinedetail.getAgeRequired()) {
                    String errorMessage = "Children's age (" + childrenAgeInYears + " years) does not meet the minimum age requirement (" + vaccinedetail.getAgeRequired() + " months) for Young Kid vaccine " + vaccinedetail.getVaccineId() + ".";
                    logger.error(errorMessage);
                   throw new InvalidDosageException(errorMessage);

                }
                validateDosageAmount(childrenAgeInYears,vaccinedetail);
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
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue().trim(), StandardCharsets.UTF_8))
                .collect(Collectors.toList()));

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData);
        vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

        String paymentUrl = vnp_Url + "?" + hashData + "&vnp_SecureHash=" + vnp_SecureHash;
        System.out.println("InitiateCheckout - VNPAY Payment URL: " + paymentUrl);
        return paymentUrl;
    }

    public String initiateCashCheckout(UUID userId, AppointmentDTO appointmentDTO) throws InvalidDosageException,CartEmptyException,MissingDataException,OutOfStockException,ResourceNotFoundException, TooManyDoseInCartException {
        List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
        if (cartItems.isEmpty()) {
            logger.error("Cart is empty for userId: {}", userId);
            throw new CartEmptyException("Cart is empty");
        }

        if (appointmentDTO == null) {
            logger.error("Appointment data is missing for userId: {}", userId);
            throw new MissingDataException("Appointment data is missing");
        }

        Set<UUID> uniqueVaccineDetailsIds = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            uniqueVaccineDetailsIds.add(cartItem.getVaccineDetailsId());
        }
        if (uniqueVaccineDetailsIds.size() > 2) {
            logger.error("Too many different vaccine doses in the cart: {} (maximum allowed is 2)", uniqueVaccineDetailsIds.size());
            throw new TooManyDoseInCartException("Only two different vaccine doses are allowed per appointment");
        }

        Optional<User> userOpt = userRepositories.findById(userId);
        if (userOpt.isEmpty()) {
            logger.error("User not found for ID: {}", userId);
            throw new ResourceNotFoundException("User not found for ID: " + userId);
        }

        double total = 0.0;
        for (CartItem cartItem : cartItems) {

            Optional<VaccineDetails> vaccineDetailsOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            if (vaccineDetailsOpt.isEmpty()) {
                logger.error("Vaccine not found for ID: {}", cartItem.getVaccineDetailsId());
                throw new ResourceNotFoundException("Vaccine not found for ID: " + cartItem.getVaccineDetailsId());
            }

            VaccineDetails vaccineDetail = vaccineDetailsOpt.get();
            if (vaccineDetail.getQuantity() < cartItem.getQuantity()) {
                logger.error("Insufficient stock for vaccine: {}", vaccineDetail.getDoseName());
                throw new OutOfStockException("Out of stock for " + vaccineDetail.getDoseName());
            }

            if (vaccineDetail.getAgeRequired() != null && appointmentDTO.getDateOfBirth() != null) {
                int childrenAgeInYears = Period.between(appointmentDTO.getDateOfBirth(), LocalDate.now()).getYears();
                if (childrenAgeInYears > vaccineDetail.getAgeRequired()) {
                    String errorMessage = "Tuổi đứa trẻ của bạn :  (" + childrenAgeInYears + " years) đang lớn hơn tuổi mà mũi tiêm này yêu cầu (" + vaccineDetail.getAgeRequired() + " years old" + vaccineDetail.getVaccineId() + ".";
                    logger.error(errorMessage);
                    throw new InvalidDosageException(errorMessage);
                }
                validateDosageAmount(childrenAgeInYears,vaccineDetail);
            }
              total += vaccineDetail.getPrice() * cartItem.getQuantity();
        }

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(UUID.randomUUID());
        appointment.setUserId(userId);
        appointment.setProcessId(UUID.randomUUID());
        appointment.setChildrenId(appointmentDTO.getChildrenId());
        appointment.setChildrenName(appointmentDTO.getChildrenName());
        appointment.setChildrenGender(appointmentDTO.getChildrenGender());
        appointment.setDateOfBirth(appointmentDTO.getDateOfBirth());
        appointment.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setTimeStart(appointmentDTO.getTimeStart());
        appointment.setStatus("Not Paid");
        appointment.setCreateAt(LocalDateTime.now());

        List<VaccineDetails> vaccineDetailsList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {

            Optional<VaccineDetails> vaccineOpt = vaccineDetailsRepository.findById(cartItem.getVaccineDetailsId());
            VaccineDetails vaccine = vaccineOpt.get();
            vaccine.setQuantity(vaccine.getQuantity() - cartItem.getQuantity());
            vaccineDetailsRepository.save(vaccine);
            VaccineDetails vaccineForAppointment = new VaccineDetails();
            vaccineForAppointment.setVaccineId(vaccine.getVaccineId());
            vaccineForAppointment.setVaccineDetailsId(vaccine.getVaccineDetailsId());
            vaccineForAppointment.setVaccinationSeriesId(UUID.randomUUID());
            vaccineForAppointment.setDoseName(vaccine.getDoseName());
            vaccineForAppointment.setManufacturer(vaccine.getManufacturer());
            vaccineForAppointment.setDateBetweenDoses(vaccine.getDateBetweenDoses());
            vaccineForAppointment.setDoseRequire(vaccine.getDoseRequire());
            vaccineForAppointment.setBoosterInterval(vaccine.getBoosterInterval());
            vaccineForAppointment.setAgeRequired(vaccine.getAgeRequired());
            vaccineForAppointment.setDosageAmount(vaccine.getDosageAmount());
            vaccineForAppointment.setPrice(vaccine.getPrice());
            vaccineForAppointment.setStatus(vaccine.getStatus());
            vaccineForAppointment.setQuantity(cartItem.getQuantity());
            vaccineForAppointment.setCurrentDose(1);
            vaccineDetailsList.add(vaccineForAppointment);
        }
        appointment.setVaccineDetailsList(vaccineDetailsList);
        boolean isFinalDose = true;
        for (VaccineDetails vaccine : vaccineDetailsList) {
            if (vaccine.getDoseRequire() > 1) {
                isFinalDose = false;
                break;
            }
        }
        appointment.setFinalDose(isFinalDose);
        appointmentRepositories.save(appointment);

        Payment payment = new Payment();
        payment.setPaymentId(UUID.randomUUID());
        payment.setUserId(userId);
        payment.setAppointmentId(appointment.getAppointmentId());
        payment.setAmount((long) total);
        payment.setStatus("Pending");
        payment.setCreatedAt(LocalDateTime.now());
        paymentsRepositories.save(payment);

        AppointmentDetail appointmentDetail = new AppointmentDetail();
        appointmentDetail.setAppointmentDetailId(UUID.randomUUID());
        appointmentDetail.setPaymentId(payment.getPaymentId());
        appointmentDetail.setAppointmentId(appointment.getAppointmentId());
        appointmentDetail.setChildrenName(appointmentDTO.getChildrenName());
        appointmentDetail.setAppointmentDate(appointmentDTO.getAppointmentDate());
        appointment.setTimeStart(appointmentDTO.getTimeStart());
        appointmentDetail.setUserId(userId);
        appointmentDetail.setPaymentMethod("Cash");
        appointmentDetail.setPaymentStatus("Pending");
        appointmentDetail.setCreateAt(LocalDateTime.now());
        appointmentDetailsRepositories.save(appointmentDetail);

        tempCart.remove(userId);
        return "Cash checkout successful. AppointmentDetail ID: " + appointmentDetail.getAppointmentDetailId();
    }

        public String processReturn(HttpServletRequest request) throws Exception {
            Map<String, String> vnp_Params = new HashMap<>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String key = entry.getKey();
                String[] values = entry.getValue();
                if (key.startsWith("vnp_")) {
                    vnp_Params.put(key, values[0]);
                }
            }
            String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");
            if (vnp_SecureHash == null) {
                throw new Exception("Missing secure hash");
            }
            String hashData = String.join("&", vnp_Params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue().trim(), StandardCharsets.UTF_8))
                    .collect(Collectors.toList()));

            System.out.println("Hash Data: " + hashData);
            System.out.println("vnp_SecureHash: " + vnp_SecureHash);
            String calculatedHash = hmacSHA512(vnp_HashSecret, hashData);
            System.out.println("Calculated Hash: " + calculatedHash);

            if (!vnp_SecureHash.equalsIgnoreCase(calculatedHash)) {
                System.out.println("Hash comparison failed:");
                System.out.println("Expected: " + vnp_SecureHash.toLowerCase());
                System.out.println("Actual  : " + calculatedHash.toLowerCase());
                throw new Exception("Invalid checksum");
            }
            if ("00".equals(vnp_Params.get("vnp_ResponseCode"))) {
                UUID userId = UUID.fromString(vnp_Params.get("vnp_OrderInfo").split("user ")[1]);
                List<CartItem> cartItems = tempCart.getOrDefault(userId, Collections.emptyList());
                if (cartItems.isEmpty()) {
                    throw new Exception("Cart is empty on return");
                }

                AppointmentDTO appointmentDTO = tempAppointments.get(userId);
                if (appointmentDTO == null) {
                    throw new Exception("Appointment data not found");
                }
                Appointment appointment = new Appointment();
                appointment.setUserId(userId);
                appointment.setAppointmentId(UUID.randomUUID());
                appointment.setProcessId(UUID.randomUUID());
                appointment.setChildrenId(appointmentDTO.getChildrenId());
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
                    VaccineDetails vaccineForAppointment = new VaccineDetails();
                    vaccineForAppointment.setVaccineId(vaccine.getVaccineId());
                    vaccineForAppointment.setVaccineDetailsId(vaccine.getVaccineDetailsId());
                    vaccineForAppointment.setVaccinationSeriesId(UUID.randomUUID());
                    vaccineForAppointment.setDoseName(vaccine.getDoseName());
                    vaccineForAppointment.setManufacturer(vaccine.getManufacturer());
                    vaccineForAppointment.setDateBetweenDoses(vaccine.getDateBetweenDoses());
                    vaccineForAppointment.setDoseRequire(vaccine.getDoseRequire());
                    vaccineForAppointment.setBoosterInterval(vaccine.getBoosterInterval());
                    vaccineForAppointment.setDosageAmount(vaccine.getDosageAmount());
                    vaccineForAppointment.setAgeRequired(vaccine.getAgeRequired());
                    vaccineForAppointment.setPrice(vaccine.getPrice());
                    vaccineForAppointment.setStatus(vaccine.getStatus());
                    vaccineForAppointment.setQuantity(cartItem.getQuantity());
                    vaccineForAppointment.setCurrentDose(1);
                    vaccineDetailsList.add(vaccineForAppointment);
                }
                appointment.setVaccineDetailsList(vaccineDetailsList);
                boolean isFinalDose = true;
                for (VaccineDetails vaccine : vaccineDetailsList) {
                    if (vaccine.getDoseRequire() > 1) {
                        isFinalDose = false;
                        break;
                    }
                }
                appointment.setFinalDose(isFinalDose);
                appointmentRepositories.save(appointment);
                AppointmentDetail appointmentDetail = new AppointmentDetail();
                appointmentDetail.setAppointmentDetailId(UUID.randomUUID());
                appointmentDetail.setChildrenName(appointmentDTO.getChildrenName());
                appointmentDetail.setAppointmentId(appointment.getAppointmentId());
                appointmentDetail.setUserId(userId);
                appointmentDetail.setPaymentMethod("VNPay");
                appointmentDetail.setPaymentStatus("Paid");
                appointmentDetail.setCreateAt(LocalDateTime.now());
                appointmentDetailsRepositories.save(appointmentDetail);
                Payment payment=new Payment();
                payment.setPaymentId(UUID.randomUUID());
                payment.setUserId(userId);
                payment.setAppointmentId(appointment.getAppointmentId());
                payment.setAmount(Long.parseLong(vnp_Params.get("vnp_Amount")) / 100);
                payment.setStatus("Success");
                payment.setVaccineDetailsList(appointment.getVaccineDetailsList());
                payment.setCreatedAt(LocalDateTime.now());
                paymentsRepositories.save(payment);
                tempCart.remove(userId);
                tempAppointments.remove(userId);
                return "appointment creation successful";

            } else {
                throw new Exception("Payment failed: " + vnp_Params.get("vnp_ResponseCode"));
            }
        }

        private String hmacSHA512(String key, String data) throws IllegalStateException {
        System.out.println("hmacSHA512 - Input data: [" + data + "]");
        return new HmacUtils("HmacSHA512", key).hmacHex(data);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff)); // Ensure unsigned byte
        }
        return sb.toString();
    }

    private void validateDosageAmount(Integer childrenAge,VaccineDetails vaccineDetail) throws InvalidDosageException {

        int dosageAmount = vaccineDetail.getDosageAmount() != null ? vaccineDetail.getDosageAmount() : 0;
        int minDosage;
        int maxDosage;

        if (childrenAge <= 2) {
            minDosage = 0;
            maxDosage = 10;
        } else if (childrenAge <= 5 && childrenAge >2) {
            minDosage = 10;
            maxDosage = 20;
        } else if (childrenAge <= 10 && childrenAge >5) {
            minDosage = 20;
            maxDosage = 30;
        } else if (childrenAge <= 18 && childrenAge >10) {
            minDosage = 15;
            maxDosage = 50;
        } else {
            minDosage = 50;
            maxDosage = 50;
        }

        if (dosageAmount < minDosage) {
            String errorMessage = String.format(
                    "Dosage amount (%d mL) is below the minimum required dosage (%d mL) for dose with age requirement of %d years for vaccine %s.",
                    dosageAmount, minDosage, childrenAge, vaccineDetail.getVaccineId()
            );
            logger.error(errorMessage);
            throw new InvalidDosageException(errorMessage);
        }

        if (dosageAmount > maxDosage) {
            String errorMessage = String.format(
                    "Dosage amount (%d mL) exceeds the maximum allowed dosage (%d mL) for vaccine with age requirement of %d years for vaccine %s.",
                    dosageAmount, maxDosage, childrenAge, vaccineDetail.getVaccineId()
            );
            logger.error(errorMessage);
            throw new InvalidDosageException(errorMessage);
        }
    }

    public class InvalidDosageException extends RuntimeException {
        public InvalidDosageException(String message) {
            super(message);
        }
    }

    public class TooManyDoseInCartException extends RuntimeException {
        public TooManyDoseInCartException(String message) {
            super(message);
        }
    }

    public class CartEmptyException extends RuntimeException {
        public CartEmptyException(String message) {
            super(message);
        }
    }

    public class MissingDataException extends RuntimeException {
        public MissingDataException(String message) {
            super(message);
        }
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    public class OutOfStockException extends RuntimeException {
        public OutOfStockException(String message) {
            super(message);
        }
    }
}
