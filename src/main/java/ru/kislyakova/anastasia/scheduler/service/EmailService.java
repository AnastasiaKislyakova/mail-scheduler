package ru.kislyakova.anastasia.scheduler.service;

import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;

import java.util.List;

public interface EmailService {
    Email createEmail(EmailCreationDto emailDto);
    Email sendEmail(int EmailId);
    List<Email> getEmails();
    Email getEmailById(int emailId);
}
