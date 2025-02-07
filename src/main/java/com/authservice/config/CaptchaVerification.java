package com.authservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class CaptchaVerification {

    @Value("${email.recaptcha.secret-key}")
    private String secretKey;
    @Value("${email.recaptcha.verify-url}")
    private String verifyUrl;

    public boolean verifyCaptcha(String userResponse) {
        try {
            String urlString = String.format("%s?secret=%s&response=%s", verifyUrl, secretKey, userResponse);
            URI uri = URI.create(urlString);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body().contains("\"success\": true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
