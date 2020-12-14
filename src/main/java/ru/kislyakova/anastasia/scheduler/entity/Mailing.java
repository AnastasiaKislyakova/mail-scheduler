package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@Table(name = "mailings")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mailing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int channelId;

    private String subject;

    private String text;

    private int attempt = 0;

//    @Embedded
//    private Schedule schedule;

    public Mailing(MailingCreationDto mailingDto) {
        this.channelId = mailingDto.getChannelId();
        this.subject = mailingDto.getSubject();
        this.text = mailingDto.getText();
       // this.schedule = mailingDto.getSchedule();
    }
}
