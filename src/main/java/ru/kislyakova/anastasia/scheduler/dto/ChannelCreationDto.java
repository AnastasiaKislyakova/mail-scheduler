package ru.kislyakova.anastasia.scheduler.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ChannelCreationDto {
    private static final String NAME_FIELD = "name";
    private static final String DESCRIPTION_FIELD = "description";
    private static final String RECIPIENTS_FIELD = "recipients";

    @NotNull(message = "Name may not be null")
    @NotBlank(message = "Name may not be blank")
    private String name;

    @NotNull(message = "Description may not be null")
    private String description;

    @NotNull(message = "Recipients may not be null")
    private List<@Email String> recipients;

    @JsonCreator
    public ChannelCreationDto(
            @JsonProperty(value = NAME_FIELD, required = true) String name,
            @JsonProperty(value = DESCRIPTION_FIELD, required = true) String description,
            @JsonProperty(value = RECIPIENTS_FIELD, required = true) List<@Email String> recipients) {
        this.name = name;
        this.description = description;
        this.recipients = recipients;
    }
}
