package ru.kislyakova.anastasia.scheduler.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum EmailStatus {
    @JsonProperty("Created")
    CREATED,
    @JsonProperty("Sent")
    SENT,
    @JsonProperty("Received")
    RECEIVED
}
