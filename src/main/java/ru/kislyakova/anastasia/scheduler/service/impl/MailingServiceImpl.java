package ru.kislyakova.anastasia.scheduler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.TeaTimeMailing;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;
import ru.kislyakova.anastasia.scheduler.service.EmailService;
import ru.kislyakova.anastasia.scheduler.service.MailingService;

import javax.mail.SendFailedException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MailingServiceImpl implements MailingService {
    private static final Logger logger = LoggerFactory.getLogger(MailingServiceImpl.class);

    private MailingRepository mailingRepository;
    private ChannelService channelService;
    private EmailService emailService;

    @Autowired
    public MailingServiceImpl(MailingRepository mailingRepository, ChannelService channelService,
                              EmailService emailService) {
        this.mailingRepository = mailingRepository;
        this.channelService = channelService;
        this.emailService = emailService;
    }

    @Override
    public Mailing createMailing(MailingCreationDto mailingDto) {
        Mailing mailing = new Mailing(mailingDto);

        //Add channelId validation

        mailingRepository.save(mailing);
        return mailing;
    }

    @Override
    public Mailing sendMailing(int mailingId) {
        Mailing mailing = mailingRepository.findById(mailingId).orElse(null);
        if (mailing == null) {
            return null;
        }

        mailing.setAttempt(mailing.getAttempt() + 1);
        sendEmails(mailing);
        mailingRepository.save(mailing);
        return mailing;
    }

    //   @Scheduled(cron = "0 17 ? * MON-FRI")
    @Scheduled(cron = "0 0/1 * * * *")
    private void sendScheduledEmails() {
        boolean sent = false;
        do {
            Mailing mailing = mailingRepository.findById(TeaTimeMailing.getMailingId()).orElse(null);
            if (mailing == null) return;
            try {
                mailing.setAttempt(mailing.getAttempt() + 1);
                sendEmails(mailing);
                sent = true;
                mailingRepository.save(mailing);
            } catch (MailException ex) {
                logger.error("Error send emails", ex);
            }

        } while (!sent);
    }

    private void sendEmails(Mailing mailing) {
        Channel channel = channelService.getChannelById(mailing.getChannelId());
        List<String> emails = channel.getRecipients();
        String subject = mailing.getSubject();
        String text = mailing.getText();

        for (String email : emails) {
            EmailCreationDto emailDto = new EmailCreationDto(mailing.getId(), mailing.getAttempt(), email, subject, text);
            Email sentEmail = emailService.sendEmail(emailDto);
            logger.info("Email to {} status: {}", email, sentEmail.getStatus());
        }
    }

    @Override
    public List<Mailing> getMailings() {
        return mailingRepository.findAll();
    }

    @Override
    public Mailing getMailingById(int mailingId) {
        return mailingRepository.findById(mailingId).orElse(null);
    }
}
