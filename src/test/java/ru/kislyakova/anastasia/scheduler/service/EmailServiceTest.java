package ru.kislyakova.anastasia.scheduler.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.kislyakova.anastasia.scheduler.config.EmailCfg;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.entity.EmailStatus;
import ru.kislyakova.anastasia.scheduler.repository.EmailRepository;
import ru.kislyakova.anastasia.scheduler.service.impl.EmailServiceImpl;

import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class EmailServiceTest {

    private EmailRepository emailRepository = Mockito.mock(EmailRepository.class);
    private JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
    private EmailCfg emailCfg = new EmailCfg();

    EmailService emailService = new EmailServiceImpl(emailRepository, emailCfg, mailSender);

    @Test
    void should_create_and_send_email() {
        EmailCreationDto emailCreationDto = new EmailCreationDto(1, 1,
                "ab@gmail.con", "Subject A", "Text A");
        Email email = new Email(emailCreationDto);
        Email saved = new Email(emailCreationDto);
        saved.setId(1);
        Email expected = new Email(emailCreationDto);
        expected.setId(1);
        expected.setStatus(EmailStatus.SENT);

        when(emailRepository.save(email)).thenReturn(saved);
        when(emailRepository.save(expected)).thenReturn(expected);

        Email actual = emailService.sendEmail(emailCreationDto);

        Mockito.verify(emailRepository, times(1)).save(email);
        Mockito.verify(emailRepository, times(1)).save(expected);
        Assertions.assertEquals(expected,actual);
    }


}
