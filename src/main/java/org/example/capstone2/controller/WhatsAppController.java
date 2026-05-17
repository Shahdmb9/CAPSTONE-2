package org.example.capstone2.controller;

import lombok.RequiredArgsConstructor;
import org.example.capstone2.service.WhatsAppService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/whatsapp")
@RequiredArgsConstructor
public class WhatsAppController {

    private final WhatsAppService whatsAppService;

    @PostMapping("/send")
    public void sendWhatsAppMessage(String phoneNumber, String message) {
        whatsAppService.sendChatMessage("0542381757", "hiiii");
    }
}
