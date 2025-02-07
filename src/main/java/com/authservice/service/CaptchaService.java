package com.authservice.service;

import com.authservice.dto.CaptchaResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class CaptchaService {

    @Value("${email.recaptcha.secret-key}")
    private String secretKey;
    @Value("${email.recaptcha.verify-url}")
    private String verifyUrl;


    public boolean verifyCaptcha(String userResponse) {
        try {
            String url = verifyUrl + "?secret=" + secretKey + "&response=" + userResponse;

            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            System.out.println("reCAPTCHA response: " + response);

            return response != null && response.contains("\"success\": true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
