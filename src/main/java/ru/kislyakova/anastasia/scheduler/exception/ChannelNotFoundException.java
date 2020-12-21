package ru.kislyakova.anastasia.scheduler.exception;

import org.springframework.http.HttpStatus;

public class ChannelNotFoundException extends SchedulerApiException {
    private final int channelId;

    public ChannelNotFoundException(int channelId) {
        super(HttpStatus.NOT_FOUND, "Not found", String.format("Channel with id '%s' not found", channelId));
        this.channelId = channelId;
    }
}
