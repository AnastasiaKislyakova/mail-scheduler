package ru.kislyakova.anastasia.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Data;

import java.util.List;

@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ChannelCreationDto {
    private String name;

    private String description;

    private List<String> recipients;
}
