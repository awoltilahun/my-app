package com.techapp.demo.controller;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    private static final String FCM_URL =
            "https://fcm.googleapis.com/v1/projects/etechpro-1b433/messages:send";

    @Autowired
    private GoogleCredentials googleCredentials;

    @PostMapping(value = "/send", produces = "application/json;charset=UTF-8")
    public ResponseEntity<Map<String, String>> sendNotification(
            @RequestParam String title,
            @RequestParam String body) {

        Map<String, String> response = new HashMap<>();

        if (googleCredentials == null) {
            response.put("status", "error");
            response.put("message", "Firebase not configured on this server");
            return new ResponseEntity<>(response, HttpStatus.SERVICE_UNAVAILABLE);
        }

        try {
            // Refresh token if expired
            googleCredentials.refreshIfExpired();
            String accessToken = googleCredentials.getAccessToken().getTokenValue();

            // Build FCM message
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", title);
            notification.put("body", body);

            Map<String, Object> message = new HashMap<>();
            message.put("topic", "all");
            message.put("notification", notification);

            Map<String, Object> payload = new HashMap<>();
            payload.put("message", message);

            // Send to FCM v1 API
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> fcmResponse = restTemplate.postForEntity(FCM_URL, entity, String.class);

            if (fcmResponse.getStatusCode() == HttpStatus.OK) {
                response.put("status", "success");
                response.put("message", "Notification sent to all users");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("status", "error");
                response.put("message", fcmResponse.getBody());
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
