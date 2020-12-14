package ru.kislyakova.anastasia.scheduler.service;

import org.springframework.mail.MailException;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;

import java.util.List;

public interface EmailService {
    Email sendEmail(EmailCreationDto emailDto) throws MailException;
    List<Email> getEmails();
    Email getEmailById(int emailId);
}
