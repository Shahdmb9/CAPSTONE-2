package org.example.capstone2.controller;

import lombok.RequiredArgsConstructor;

import org.example.capstone2.service.MailService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {
    private final MailService mailService;

    @PostMapping("/send")
    public void sendMail(@RequestParam String to, @RequestParam String subject, @RequestParam String body) {
        mailService.sendPlainText(to, subject, body);
    }
}
