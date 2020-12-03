package ru.kislyakova.anastasia.scheduler.service;

import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;

import java.util.List;

public interface MailingService {
    Mailing createMailing(MailingCreationDto mailingDto);
    Mailing sendMailing(int mailingId);
    List<Mailing> getMailings();
    Mailing getMailingById(int mailingId);
}
