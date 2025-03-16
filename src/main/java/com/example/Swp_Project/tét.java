package com.example.Swp_Project;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class tét {
    public static void main(String[] args) throws Exception {
        String secret = "4046G9C8YY9H8WFSJG8AVB9VZNTT1D68";
        String data = "vnp_Amount=60000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14848249&vnp_CardType=ATM&vnp_OrderInfo=Thanh toan don hang cho user 01b86c56-40ea-47c8-afaa-c4866213aa2b&vnp_PayDate=20250316185843&vnp_ResponseCode=00&vnp_TmnCode=7QRTMNBH&vnp_TransactionNo=14848249&vnp_TransactionStatus=00&vnp_TxnRef=984286e8";
        String hash = hmacSHA512(secret, data);
        System.out.println("Test Hash: " + hash);
    }

    private static String hmacSHA512(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] hmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmac);
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
