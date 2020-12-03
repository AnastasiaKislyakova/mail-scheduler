package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "channels")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    private String description;

    @ElementCollection
    @CollectionTable(
            name="channel_recipients",
            joinColumns=@JoinColumn(name="channel_id")
    )
    @Column(name="recipients")
    private List<String> recipients;

    public Channel(String name, String description, List<String> recipients) {
        this.name = name;
        this.description = description;
        this.recipients = recipients;
    }
}
