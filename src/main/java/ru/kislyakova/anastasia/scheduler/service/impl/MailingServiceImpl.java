package ru.kislyakova.anastasia.scheduler.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;
import ru.kislyakova.anastasia.scheduler.service.MailingService;

import java.util.List;

@Service
public class MailingServiceImpl implements MailingService {
    private MailingRepository mailingRepository;

    @Autowired
    public MailingServiceImpl(MailingRepository mailingRepository) {
        this.mailingRepository = mailingRepository;
    }

    @Override
    public Mailing createMailing(MailingCreationDto mailingDto) {
        Mailing mailing = new Mailing(mailingDto);
        mailingRepository.save(mailing);
        return mailing;
    }

    @Override
    public Mailing sendMailing(int mailingId) {
        Mailing mailing = mailingRepository.findById(mailingId).orElse(null);
        if (mailing == null) {
            return null;
        }
        //send Emails
        return mailing;
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
