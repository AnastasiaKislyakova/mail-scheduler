package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

@Data
@Entity
@Table(name = "schedules")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private LocalDate from;

    @Column(nullable = true)
    private LocalDate to;

    private Duration duration;

    public Schedule(LocalDate from, LocalDate to, Duration duration) {
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
