package ru.kislyakova.anastasia.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kislyakova.anastasia.scheduler.entity.EmailStatus;

@Data
@NoArgsConstructor
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class EmailCreationDto {
    private Integer mailingId;

    private Integer mailingAttempt;

    private String recipient;

    private String subject;

    private String text;

    @NotNull
    private EmailStatus status = EmailStatus.CREATED;

    public EmailCreationDto(Integer mailingId, Integer mailingAttempt, String recipient,
                            String subject, String text) {
        this.mailingId = mailingId;
        this.mailingAttempt = mailingAttempt;
        this.recipient = recipient;
        this.subject = subject;
        this.text = text;
    }
}
