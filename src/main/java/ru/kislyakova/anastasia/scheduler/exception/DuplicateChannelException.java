package ru.kislyakova.anastasia.scheduler.exception;

import org.springframework.http.HttpStatus;

public class DuplicateChannelException extends SchedulerApiException {
    private final String chanelName;

    public DuplicateChannelException(String chanelName) {
        super(HttpStatus.CONFLICT, "DuplicateChanel", String.format("Channel with name '%s' already exists", chanelName));
        this.chanelName = chanelName;
    }
}
