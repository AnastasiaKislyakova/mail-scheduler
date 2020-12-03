package ru.kislyakova.anastasia.scheduler.entity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;

import javax.persistence.*;

@Data
@Entity
@Table(name = "emails")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String recipient;

    private String subject;

    private String text;

    private EmailStatus status;

    public Email(EmailCreationDto emailDto) {
        this.recipient = emailDto.getRecipient();
        this.subject = emailDto.getSubject();
        this.text = emailDto.getText();
        this.status = emailDto.getStatus();
    }
}
