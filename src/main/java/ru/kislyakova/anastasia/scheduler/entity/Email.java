package ru.kislyakova.anastasia.scheduler.entity;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

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

    private String title;

    private String text;

    private EmailStatus status;

    public Email(String recipient, String title, String text, EmailStatus status) {
        this.recipient = recipient;
        this.title = title;
        this.text = text;
        this.status = status;
    }
}
