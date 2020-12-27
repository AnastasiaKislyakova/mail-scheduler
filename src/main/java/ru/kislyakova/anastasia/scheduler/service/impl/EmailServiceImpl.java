package ru.kislyakova.anastasia.scheduler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.config.EmailCfg;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.entity.EmailStatus;
import ru.kislyakova.anastasia.scheduler.repository.EmailRepository;
import ru.kislyakova.anastasia.scheduler.service.EmailService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private EmailRepository emailRepository;
    private EmailCfg emailCfg;
    private JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository, EmailCfg emailCfg, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.emailCfg = emailCfg;
        this.mailSender = mailSender;
    }

    @Override
    public Email sendEmail(EmailCreationDto emailDto) {
        Email email = new Email(emailDto);
        try {
            email = emailRepository.save(email);
        } catch (DataIntegrityViolationException ex) {
            email = emailRepository.findByMailingIdAttemptAndRecipient(emailDto.getMailingId(),
                    emailDto.getMailingAttempt(), emailDto.getRecipient());
            if (email.getStatus() == EmailStatus.SENT) return email;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(this.emailCfg.getUsername());
        mailMessage.setTo(email.getRecipient());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getText());

        //try {
        mailSender.send(mailMessage);
        email.setStatus(EmailStatus.SENT);
        email = emailRepository.save(email);
//        }
//        catch(MailException ex) {
//            logger.error("Couldn't send email, caused by MailException : {}", ex.getMessage());
//        }

        return email;
    }

    //TODO should add sendEmails ?
    public List<Email> sendEmails(List<EmailCreationDto> emailDtos) {
        List<SimpleMailMessage> messages = new ArrayList<>();
        List<Email> emails = new ArrayList<>();
        for (EmailCreationDto emailDto : emailDtos){
            Email email = new Email(emailDto);
            try {
                email = emailRepository.save(email);
            } catch (DataIntegrityViolationException ex) {
                email = emailRepository.findByMailingIdAttemptAndRecipient(emailDto.getMailingId(),
                        emailDto.getMailingAttempt(), emailDto.getRecipient());
                if (email.getStatus() == EmailStatus.SENT) continue;
            }

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(this.emailCfg.getUsername());
            mailMessage.setTo(email.getRecipient());
            mailMessage.setSubject(email.getSubject());
            mailMessage.setText(email.getText());

            messages.add(mailMessage);
            emails.add(email);
        }

        SimpleMailMessage[] simpleMailMessages = new SimpleMailMessage[messages.size()];
        messages.toArray(simpleMailMessages);
        mailSender.send(simpleMailMessages);

        for (Email email : emails) {
            email.setStatus(EmailStatus.SENT);
            emailRepository.save(email);
        }

        return emails;
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
