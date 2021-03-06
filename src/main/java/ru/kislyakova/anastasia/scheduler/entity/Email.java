package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "emails")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="mailing_id", nullable = true)
    private Integer mailingId;

    @Column(name="mailing_attempt", nullable = true)
    private Integer mailingAttempt;

    @javax.validation.constraints.Email
    private String recipient;

    private String subject;

    private String text;

    @Enumerated(value = EnumType.STRING)
    private EmailStatus status = EmailStatus.CREATED;

    public Email(EmailCreationDto emailDto) {
        this.mailingId = emailDto.getMailingId();
        this.mailingAttempt = emailDto.getMailingAttempt();
        this.recipient = emailDto.getRecipient();
        this.subject = emailDto.getSubject();
        this.text = emailDto.getText();
        this.status = emailDto.getStatus();
    }
}
