package ru.kislyakova.anastasia.scheduler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.repository.EmailRepository;
import ru.kislyakova.anastasia.scheduler.service.EmailService;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private EmailRepository emailRepository;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Email createEmail(EmailCreationDto emailDto) {
        Email email = new Email(emailDto);
        emailRepository.save(email);
        return null;
    }

    @Override
    public Email sendEmail(int EmailId) {
        return null;
    }

    @Override
    public List<Email> getEmails() {
        return emailRepository.findAll();
    }

    @Override
    public Email getEmailById(int emailId) {
        return emailRepository.findById(emailId).orElse(null);
    }
}
