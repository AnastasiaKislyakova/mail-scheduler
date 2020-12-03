package ru.kislyakova.anastasia.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;
import ru.kislyakova.anastasia.scheduler.entity.Schedule;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MailingCreationDto {
    private int channelId;

    private String subject;

    private String text;

    private Schedule schedule;
}
