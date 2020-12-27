package ru.kislyakova.anastasia.scheduler.utils;

import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;

import java.util.Arrays;
import java.util.List;

public class EntityUtils {
    private static ChannelCreationDto channelCreationDto1 = new ChannelCreationDto("A", "channel A",
            Arrays.asList("ab@gmail.com", "abc@gmail.com"));

    private static ChannelCreationDto channelCreationDto2 = new ChannelCreationDto("B", "channel B",
            Arrays.asList("ab@gmail.com", "abc@gmail.com"));

    private static ChannelUpdatingDto channelUpdatingDto = new ChannelUpdatingDto("channel A",
            Arrays.asList("ab@gmail.com", "abc@gmail.com", "abcd@gmail.com"));

    private static MailingCreationDto mailingCreationDto1 = new MailingCreationDto(1, "Subject A",
            "Text A");

    private static MailingCreationDto mailingCreationDto2 = new MailingCreationDto(2, "Subject B",
            "Text B");

    private static EmailCreationDto emailCreationDto1 = new EmailCreationDto(1, 1,
            "ab@gmail.com", "Subject A", "Text A");

    private static EmailCreationDto emailCreationDto2 = new EmailCreationDto(1, 1,
            "abc@gmail.com", "Subject A", "Text A");

    public static ChannelCreationDto channelCreationDto(){
        return channelCreationDto1;
    }

    public static Channel channel() {
        return new Channel(channelCreationDto1);
    }

    public static ChannelUpdatingDto channelUpdatingDto(){
        return channelUpdatingDto;
    }

    public static List<Channel> channels() {
        Channel channel1 = new Channel(channelCreationDto1);
        channel1.setId(1);
        Channel channel2 = new Channel(channelCreationDto2);
        channel2.setId(2);
        return Arrays.asList(channel1, channel2);
    }

    public static MailingCreationDto mailingCreationDto() { return mailingCreationDto1; }

    public static Mailing mailing() { return new Mailing(mailingCreationDto1); }

    public static List<Mailing> mailings() {
        Mailing mailing1 = new Mailing(mailingCreationDto1);
        mailing1.setId(1);
        Mailing mailing2 = new Mailing(mailingCreationDto2);
        mailing2.setId(2);
        return Arrays.asList(mailing1, mailing2);
    }

    public static EmailCreationDto emailCreationDto() { return emailCreationDto1; }

    public static Email email() { return new Email(emailCreationDto1); }

    public static List<Email> emails() {
        Email email1 = new Email(emailCreationDto1);
        email1.setId(1);
        Email email2 = new Email(emailCreationDto2);
        email2.setId(2);
        return Arrays.asList(email1, email2);
    }
}
