package ru.kislyakova.anastasia.scheduler.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.kislyakova.anastasia.scheduler.config.EmailCfg;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.entity.EmailStatus;
import ru.kislyakova.anastasia.scheduler.repository.EmailRepository;
import ru.kislyakova.anastasia.scheduler.service.impl.EmailServiceImpl;
import ru.kislyakova.anastasia.scheduler.utils.EntityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class EmailServiceTest {

    private EmailRepository emailRepository = Mockito.mock(EmailRepository.class);
    private JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
    private EmailCfg emailCfg = Mockito.mock(EmailCfg.class);

    private EmailService emailService = new EmailServiceImpl(emailRepository, emailCfg, mailSender);

    @Test
    void should_create_and_send_email() {
        EmailCreationDto emailCreationDto = EntityUtils.emailCreationDto();
        Email email = new Email(emailCreationDto);
        Email saved = new Email(emailCreationDto);
        saved.setId(1);
        Email expected = new Email(emailCreationDto);
        expected.setId(1);
        expected.setStatus(EmailStatus.SENT);

        when(emailRepository.save(email)).thenReturn(saved);
        when(emailRepository.save(expected)).thenReturn(expected);

        Email actual = emailService.sendEmail(emailCreationDto);

        verify(emailRepository, times(1)).save(email);
        verify(emailRepository, times(1)).save(expected);
        assertEquals(expected,actual);
    }

    //TODO why doesn't it work?
    @Test
    void should_create_and_not_send_email() {
        EmailCreationDto emailCreationDto = EntityUtils.emailCreationDto();
        Email email = new Email(emailCreationDto);
        Email saved = new Email(emailCreationDto);
        saved.setId(1);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        String from = "noreply@gamil.com";
        mailMessage.setFrom(from);
        mailMessage.setTo(email.getRecipient());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getText());

        when(emailRepository.save(email)).thenReturn(saved);
        when(emailCfg.getUsername()).thenReturn(from);
        doThrow(MailException.class).when(mailSender).send(mailMessage);

        assertThrows(MailException.class, () -> emailService.sendEmail(emailCreationDto));

        verify(emailRepository, times(1)).save(email);
    }

    @Test
    void should_not_send_sent_email() {
        EmailCreationDto emailCreationDto = EntityUtils.emailCreationDto();
        Email email = new Email(emailCreationDto);
        Email emailFromDB = new Email(emailCreationDto);
        emailFromDB.setId(1);
        emailFromDB.setStatus(EmailStatus.SENT);

        when(emailRepository.save(email)).thenThrow(DataIntegrityViolationException.class);
        when(emailRepository.findByMailingIdAttemptAndRecipient(emailCreationDto.getMailingId(),
                emailCreationDto.getMailingAttempt(), emailCreationDto.getRecipient()))
                .thenReturn(emailFromDB);

        Email actual = emailService.sendEmail(emailCreationDto);

        assertEquals(emailFromDB, actual);
    }

    @Test
    void should_get_email_by_id() {
        Email email = EntityUtils.email();
        email.setId(1);

        when(emailRepository.findById(1)).thenReturn(Optional.of(email));

        Email actual = emailService.getEmailById(1);

        assertEquals(email, actual);
    }

    @Test
    void should_not_find_email_by_id() {
        when(emailRepository.findById(1)).thenReturn(Optional.empty());

        Email actual = emailService.getEmailById(1);

        assertNull(actual);
    }

    @Test
    void should_get_emails() {
        List<Email> emails = EntityUtils.emails();

        when(emailRepository.findAll()).thenReturn(emails);

        List<Email> actual = emailService.getEmails();

        assertEquals(emails, actual);
    }


}
