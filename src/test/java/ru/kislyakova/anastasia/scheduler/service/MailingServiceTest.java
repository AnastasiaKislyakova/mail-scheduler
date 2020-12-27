package ru.kislyakova.anastasia.scheduler.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.entity.EmailStatus;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.exception.ChannelNotFoundException;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;
import ru.kislyakova.anastasia.scheduler.service.impl.MailingServiceImpl;
import ru.kislyakova.anastasia.scheduler.utils.EntityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class MailingServiceTest {
    private ChannelService channelService = Mockito.mock(ChannelService.class);
    private EmailService emailService = Mockito.mock(EmailService.class);
    private MailingRepository mailingRepository = Mockito.mock(MailingRepository.class);

    private MailingService mailingService = new MailingServiceImpl(mailingRepository, channelService, emailService);

    @Test
    void should_create_mailing() {
        MailingCreationDto mailingCreationDto = EntityUtils.mailingCreationDto();
        Mailing saved = new Mailing(mailingCreationDto);
        Mailing expected = new Mailing(mailingCreationDto);
        expected.setId(1);
        Channel channel = EntityUtils.channel();
        channel.setId(1);

        when(channelService.getChannelById(1)).thenReturn(channel);
        when(mailingRepository.save(saved)).thenReturn(expected);

        Mailing actual = mailingService.createMailing(mailingCreationDto);

        verify(channelService, times(1)).getChannelById(1);
        verify(mailingRepository, times(1)).save(saved);
        assertEquals(expected, actual);
        assertNotEquals(saved.getId(), actual.getId());
    }

    @Test
    void should_not_create_mailing_with_not_found_channel() {
        MailingCreationDto mailingCreationDto = EntityUtils.mailingCreationDto();
        Mailing saved = new Mailing(mailingCreationDto);

        when(channelService.getChannelById(1)).thenReturn(null);

        Assertions.assertThrows(ChannelNotFoundException.class, () -> mailingService.createMailing(mailingCreationDto));

        verify(channelService, times(1)).getChannelById(1);
        verify(mailingRepository, times(0)).save(saved);
    }

    @Test
    void should_send_mailing() {
        MailingCreationDto mailingCreationDto = EntityUtils.mailingCreationDto();
        Channel channel = EntityUtils.channel();
        channel.setId(1);

        Mailing mailing = new Mailing(mailingCreationDto);
        mailing.setId(1);
        mailing.setAttempt(0);

        Mailing expected = new Mailing(mailingCreationDto);
        expected.setId(1);
        expected.setAttempt(1);

        EmailCreationDto emailCreationDto1 = new EmailCreationDto(mailing.getId(), expected.getAttempt(),
                channel.getRecipients().get(0), mailing.getSubject(), mailing.getText());
        Email email1 = new Email(emailCreationDto1);
        email1.setId(1);
        email1.setStatus(EmailStatus.SENT);

        EmailCreationDto emailCreationDto2 = new EmailCreationDto(mailing.getId(), expected.getAttempt(),
                channel.getRecipients().get(1), mailing.getSubject(), mailing.getText());
        Email email2 = new Email(emailCreationDto2);
        email2.setId(2);
        email2.setStatus(EmailStatus.SENT);

        when(mailingRepository.findById(1)).thenReturn(Optional.of(mailing));
        when(channelService.getChannelById(1)).thenReturn(channel);
        when(emailService.sendEmail(emailCreationDto1)).thenReturn(email1);
        when(emailService.sendEmail(emailCreationDto2)).thenReturn(email2);
        when(mailingRepository.save(mailing)).thenReturn(expected);

        Mailing actual = mailingService.sendMailing(1);

        verify(mailingRepository, times(1)).findById(1);
        verify(channelService, times(1)).getChannelById(1);
        verify(emailService, times(1)).sendEmail(emailCreationDto1);
        verify(emailService, times(1)).sendEmail(emailCreationDto2);
        verify(mailingRepository, times(1)).save(expected);
        assertEquals(expected, actual);
    }

    @Test
    void should_not_send_mailing_with_not_found_id() {
        when(mailingRepository.findById(1)).thenReturn(Optional.empty());

        Mailing actual = mailingService.sendMailing(1);

        verify(mailingRepository, times(1)).findById(1);
        assertNull(actual);
    }

    @Test
    void should_not_send_mailing_with_not_found_channel() {
        Mailing mailing = EntityUtils.mailing();
        mailing.setId(1);
        mailing.setAttempt(0);

        when(mailingRepository.findById(1)).thenReturn(Optional.of(mailing));
        when(channelService.getChannelById(1)).thenReturn(null);

        assertThrows(ChannelNotFoundException.class, () -> mailingService.sendMailing(1));

        verify(mailingRepository, times(1)).findById(1);
        verify(channelService, times(1)).getChannelById(1);
    }

    @Test
    void should_get_mailing_by_id() {
        Mailing expected = EntityUtils.mailing();
        expected.setId(1);

        when(mailingRepository.findById(1)).thenReturn(Optional.of(expected));

        Mailing actual = mailingService.getMailingById(1);

        verify(mailingRepository, times(1)).findById(1);
        assertEquals(expected, actual);
    }

    @Test
    void should_not_find_mailing_by_id() {
        when(mailingRepository.findById(1)).thenReturn(Optional.empty());

        Mailing actual = mailingService.getMailingById(1);

        Mockito.verify(mailingRepository, times(1)).findById(1);
        Assertions.assertNull(actual);
    }

    @Test
    void should_get_mailings() {
        List<Mailing> expected = EntityUtils.mailings();

        when(mailingRepository.findAll()).thenReturn(expected);

        List<Mailing> actual = mailingService.getMailings();

        verify(mailingRepository, times(1)).findAll();
        assertEquals(expected, actual);
    }
}
