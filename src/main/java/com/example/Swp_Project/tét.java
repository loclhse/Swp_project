package com.example.Swp_Project;

import org.apache.commons.codec.digest.HmacUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class tét {
    public static void main(String[] args) throws Exception {
        String secret = "4046G9C8YY9H8WFSJG8AVB9VZNTT1D68";
        String data = "vnp_Amount=60000000&vnp_BankCode=NCB&vnp_BankTranNo=VNP14848574&vnp_CardType=ATM&vnp_OrderInfo=Thanh toan don hang cho user 01b86c56-40ea-47c8-afaa-c4866213aa2b&vnp_PayDate=20250316225502&vnp_ResponseCode=00&vnp_TmnCode=7QRTMNBH&vnp_TransactionNo=14848574&vnp_TransactionStatus=00&vnp_TxnRef=4408c96f";
        System.out.println("HmacUtils Test Hash: " + HmacUtils.hmacSha512Hex(secret, data));
    }
}
