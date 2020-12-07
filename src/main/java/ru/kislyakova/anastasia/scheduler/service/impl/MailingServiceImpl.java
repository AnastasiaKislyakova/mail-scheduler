package ru.kislyakova.anastasia.scheduler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;
import ru.kislyakova.anastasia.scheduler.service.EmailService;
import ru.kislyakova.anastasia.scheduler.service.MailingService;

import java.util.List;

@Service
public class MailingServiceImpl implements MailingService {
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

        sendEmails(mailing);
        return mailing;
    }

 //   @Scheduled(cron = "0 17 ? * MON-FRI")
    @Scheduled(cron = "0 0/1 * * * *")
    private void sendScheduledEmails() {
        List<Mailing> mailings = mailingRepository.findAll();
        if (!mailings.isEmpty()) {
            Mailing mailing = mailings.get(0);
            sendEmails(mailing);
        }
    }

    private void sendEmails(Mailing mailing) {
        Channel channel = channelService.getChannelById(mailing.getChannelId());
        List<String> emails =  channel.getRecipients();
        String subject = mailing.getSubject();
        String text = mailing.getText();

        for (String email : emails) {
            EmailCreationDto emailDto = new EmailCreationDto(email, subject, text);
            emailService.createEmail(emailDto);
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
