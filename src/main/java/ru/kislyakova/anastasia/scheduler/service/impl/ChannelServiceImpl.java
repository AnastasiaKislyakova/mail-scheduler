package ru.kislyakova.anastasia.scheduler.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;

import java.util.List;

@Service
public class ChannelServiceImpl implements ChannelService {
    private static final Logger logger = LoggerFactory.getLogger(ChannelServiceImpl.class);

    private ChannelRepository channelRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(ChannelCreationDto channelCreationDto) {
        Channel channel = new Channel(channelCreationDto);
        channel = channelRepository.save(channel);
        return channel;
    }

    @Override
    public List<Channel> getChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel getChannelById(int channelId) {
        return channelRepository.findById(channelId).orElse(null);
    }

    @Override
    public Channel updateChannel(int channelId, ChannelUpdatingDto channelDto) {
        Channel channel = channelRepository.findById(channelId)
                .orElse(null);
        if (channel == null) {
            return null;
        }
        channel.setDescription(channelDto.getDescription());
        channel.setRecipients(channelDto.getRecipients());
        channelRepository.save(channel);
        return channel;
    }
}
