package ru.kislyakova.anastasia.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.kislyakova.anastasia.scheduler.entity.Schedule;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@JsonAutoDetect
public class MailingCreationDto {

    @Min(1)
    private int channelId;

    @NotNull
    private String subject;

    @NotNull
    private String text;

   // private Schedule schedule;
}
