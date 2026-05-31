package org.example.capstone2.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Service
public class WhatsAppService {

    private final String INSTANCE_ID = "instance178546";
    private final String TOKEN = "mnt104j2xaztvrp2";
    private final String API_URL = "https://api.ultramsg.com/" + INSTANCE_ID + "/messages/chat";

    public String sendChatMessage(String to, String body) {
        RestTemplate restTemplate = new RestTemplate();

        // Ultramsg expects application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("token", TOKEN);
        map.add("to", to); // Phone number with country code (e.g., +123456789)
        map.add("body", body);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        System.out.println(request);
        ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
        return response.getBody();
    }
}
