package ru.kislyakova.anastasia.scheduler.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.service.MailingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/mailings")
public class MailingController {
    private MailingService mailingService;

    @Autowired
    public MailingController(MailingService mailingService) {
        this.mailingService = mailingService;
    }

    @PostMapping
    ResponseEntity<Mailing> createMailing(@RequestBody @Valid MailingCreationDto mailingCreationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mailingService.createMailing(mailingCreationDto));
    }

    @PostMapping(value = "{mailingId}/send")
    ResponseEntity<Mailing> sendMailing(@PathVariable int mailingId) {
        Mailing mailingById = mailingService.sendMailing(mailingId);
        if (mailingById == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mailingById);
    }

    @GetMapping
    List<Mailing> getMailings() {
        return mailingService.getMailings();
    }

    @GetMapping(value = "{mailingId}")
    ResponseEntity<Mailing> getMailingById(@PathVariable int mailingId) {
        Mailing mailingById = mailingService.getMailingById(mailingId);
        if (mailingById == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mailingById);
    }
}
