package ru.kislyakova.anastasia.scheduler.service;

import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;

import java.util.List;

public interface ChannelService {
    Channel createChannel(ChannelCreationDto channelCreationDto);
    List<Channel> getChannels();
    Channel getChannelById(int channelId);
    Channel updateChannel(int channelId, ChannelUpdatingDto channelDto);
}
