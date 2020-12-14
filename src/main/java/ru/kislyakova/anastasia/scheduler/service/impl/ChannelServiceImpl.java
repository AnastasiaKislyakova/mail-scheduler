package ru.kislyakova.anastasia.scheduler.service.impl;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.exception.DuplicateChannelException;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;

import java.util.List;

@Service
public class ChannelServiceImpl implements ChannelService {

    private ChannelRepository channelRepository;

    @Autowired
    public ChannelServiceImpl(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Cacheable(value = "channels")
    @Override
    public Channel createChannel(ChannelCreationDto channelCreationDto) {
        Channel channel = new Channel(channelCreationDto);
        try {
            channel = channelRepository.save(channel);
        } catch (Exception ex) {
            if (ex.getCause() instanceof ConstraintViolationException) {
                throw new DuplicateChannelException(channelCreationDto.getName());
            } else {
                throw ex;
            }
        }
        return channel;
    }

    @Override
    public List<Channel> getChannels() {
        return channelRepository.findAll();
    }

    @Cacheable(value = "channels")
    @Override
    public Channel getChannelById(int channelId) {
        Channel channel = channelRepository.findById(channelId).orElse(null);

        return channel;
    }

    @CachePut(value = "channels", key = "#channelId")
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
