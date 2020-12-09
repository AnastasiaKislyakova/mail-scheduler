package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.List;

@Data
@NoArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
@Table(name = "channels")
public class Channel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name", unique = true)
    private String name;

    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name="channel_recipients",
            joinColumns=@JoinColumn(name="channel_id")
    )
    @Column(name="recipient_address")
    private List<String> recipients;

    public Channel(ChannelCreationDto channel) {
        this.name = channel.getName();
        this.description = channel.getDescription();
        this.recipients = channel.getRecipients();
    }
}
