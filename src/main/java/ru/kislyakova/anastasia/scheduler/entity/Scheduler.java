package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "schedulers")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Scheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int channelId;

    private String title;

    private String text;

    private int scheduleId;

    public Scheduler(int channelId, String title, String text, int scheduleId) {
        this.channelId = channelId;
        this.title = title;
        this.text = text;
        this.scheduleId = scheduleId;
    }
}
