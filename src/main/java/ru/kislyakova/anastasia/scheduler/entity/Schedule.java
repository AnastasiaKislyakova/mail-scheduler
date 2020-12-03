package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Embeddable
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private LocalDateTime from;

    @Column(nullable = true)
    private LocalDateTime to;

    private Duration duration;

    public Schedule(LocalDateTime from, LocalDateTime to, Duration duration) {
        this.from = from;
        this.to = to;
        this.duration = duration;
    }

    public Optional getFrom() {
        return Optional.ofNullable(from);
    }

    public Optional getTo() {
        return Optional.ofNullable(to);
    }
}
