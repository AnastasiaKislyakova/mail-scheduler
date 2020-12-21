package ru.kislyakova.anastasia.scheduler;

import org.hibernate.exception.ConstraintViolationException;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;

import java.util.Arrays;
import java.util.List;

public class TeaTimeMailing {
    private static int MAILING_ID;

    void create(ChannelRepository channelRepository, MailingRepository mailingRepository) {
        ChannelCreationDto channelDto = new ChannelCreationDto("Время чая", "Любители попить чай",
                Arrays.asList("anastasia.kislyakova@gmail.com", "petrovvodkin.kuzma@gmail.com"));
        Channel channel = new Channel(channelDto);
        try {
            channel = channelRepository.save(channel);
        }
         catch (Exception ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                channel = channelRepository.findByName(channelDto.getName());
            } else {
                throw ex;
            }
        }
        int channelId = channel.getId();
        MailingCreationDto mailingDto = new MailingCreationDto(channelId, "Время чая", "Пора пить чай!");
        Mailing mailing = new Mailing(mailingDto);
        List<Mailing> foundMailings = mailingRepository
                .findByChannelIdAndText(mailing.getChannelId(), mailing.getText());
        if (!foundMailings.isEmpty()) {
            mailing = mailingRepository.save(foundMailings.get(0));
        } else {
            mailing = mailingRepository.save(mailing);
        }
        MAILING_ID = mailing.getId();
    }

    public static int getMailingId() {
        return MAILING_ID;
    }

}
