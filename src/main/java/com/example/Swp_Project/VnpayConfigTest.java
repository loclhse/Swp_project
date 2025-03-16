package com.example.Swp_Project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class VnpayConfigTest {
    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl = "https://vaccinemanagement-2f854cbfd074.herokuapp.com/api/cart/return";
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource("application.properties"));
        return configurer;
    }
    public static void main(String[] args) {
        // Start Spring context
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(VnpayConfigTest.class);

        // Get the bean instance
        VnpayConfigTest config = context.getBean(VnpayConfigTest.class);

        // Print the values
        System.out.println("Testing @Value in static main:");
        System.out.println("vnp_TmnCode: " + config.vnp_TmnCode);
        System.out.println("vnp_HashSecret: " + config.vnp_HashSecret);
        System.out.println("vnp_Url: " + config.vnp_Url);
        System.out.println("vnp_ReturnUrl: " + config.vnp_ReturnUrl);

        // Close context
        context.close();
    }
}
