package ru.kislyakova.anastasia.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.service.EmailService;

import java.util.List;

@RestController
@RequestMapping("api/emails")
public class EmailController {
    private EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping
    ResponseEntity<Email> sendEmail(@RequestBody EmailCreationDto emailCreationDto) {
        return ResponseEntity.ok(emailService.sendEmail(emailCreationDto));
    }

    @GetMapping
    ResponseEntity<List<Email>> getEmails() {
        return ResponseEntity.ok(emailService.getEmails());
    }

    @GetMapping(value = "{emailId}")
    ResponseEntity<Email> getEmailById(@PathVariable int emailId) {
        Email emailById = emailService.getEmailById(emailId);
        if (emailById == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(emailById);
    }
}
