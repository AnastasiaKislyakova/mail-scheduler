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
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private EmailRepository emailRepository;
    private EmailCfg emailCfg;
   // private JavaMailSender mailSender;

    @Autowired
    public EmailServiceImpl(EmailRepository emailRepository, EmailCfg emailCfg, JavaMailSender mailSender) {
        this.emailRepository = emailRepository;
        this.emailCfg = emailCfg;
       // this.mailSender = mailSender;
    }

    @Override
    public Email sendEmail(EmailCreationDto emailDto) {
        Email email = new Email(emailDto);
        emailRepository.save(email);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(this.emailCfg.getHost());
        mailSender.setPort(this.emailCfg.getPort());
        mailSender.setUsername(this.emailCfg.getUsername());
        mailSender.setPassword(this.emailCfg.getPassword());

        Properties props = mailSender.getJavaMailProperties();
//        props.put("mail.transport.protocol", this.emailCfg.getTransportProtocol());
//        props.put("mail.smtp.auth", this.emailCfg.getAuth());
//        props.put("mail.smtp.starttls.enable", this.emailCfg.getStarttlsEnable());
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);

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
    public List<Email> getEmails() {
        return emailRepository.findAll();
    }

    @Override
    public Email getEmailById(int emailId) {
        return emailRepository.findById(emailId).orElse(null);
    }
}
