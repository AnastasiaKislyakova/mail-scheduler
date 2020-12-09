package ru.kislyakova.anastasia.scheduler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private EmailRepository emailRepository;
    private EmailCfg emailCfg;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository, EmailCfg emailCfg, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.emailCfg = emailCfg;
    }

    @Override
    public Email createEmail(EmailCreationDto emailDto) {
        Email email = new Email(emailDto);
        emailRepository.save(email);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.emailCfg.getHost());
        mailSender.setPort(this.emailCfg.getPort());
        mailSender.setUsername(this.emailCfg.getUsername());
        mailSender.setPassword(this.emailCfg.getPassword());

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(this.emailCfg.getUsername());
        mailMessage.setTo(email.getRecipient());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getText());

        //try {
            mailSender.send(mailMessage);
            email.setStatus(EmailStatus.SENT);
            emailRepository.save(email);
//        }
//        catch(MailException ex) {
//            logger.error("Couldn't send email, caused by MailException : {}", ex.getMessage());
//        }

        return email;
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
