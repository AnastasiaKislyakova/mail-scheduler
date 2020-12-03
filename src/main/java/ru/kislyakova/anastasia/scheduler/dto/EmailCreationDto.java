package ru.kislyakova.anastasia.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.sun.istack.NotNull;
import lombok.Data;
import ru.kislyakova.anastasia.scheduler.entity.EmailStatus;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EmailCreationDto {
    private String recipient;

    private String subject;

    private String text;

    @NotNull
    private EmailStatus status = EmailStatus.CREATED;
}
