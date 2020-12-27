package ru.kislyakova.anastasia.scheduler.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.exception.DuplicateChannelException;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.service.impl.ChannelServiceImpl;
import ru.kislyakova.anastasia.scheduler.utils.EntityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;


public class ChannelServiceTest {

    private ChannelRepository channelRepository = Mockito.mock(ChannelRepository.class);

    private ChannelService channelService = new ChannelServiceImpl(channelRepository);

    @Test
    void should_create_channel() {
        ChannelCreationDto channelDto = EntityUtils.channelCreationDto();
        Channel saved = new Channel(channelDto);
        Channel returned = new Channel(channelDto);
        returned.setId(1);

        when(channelRepository.save(saved)).thenReturn(returned);

        Channel actual = channelService.createChannel(channelDto);

        verify(channelRepository, times(1)).save(saved);
        assertEquals(returned, actual);
        assertNotEquals(saved.getId(), actual.getId());
    }

    @Test
    void should_not_create_channel_with_existing_name() {
        ChannelCreationDto channelDto = EntityUtils.channelCreationDto();
        Channel saved = new Channel(channelDto);

        when(channelRepository.save(saved)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(DuplicateChannelException.class, () -> channelService.createChannel(channelDto));

        verify(channelRepository, times(1)).save(saved);
    }

    @Test
    void should_update_channel() {
        ChannelUpdatingDto channelUpdatingDto = EntityUtils.channelUpdatingDto();
        Channel original = EntityUtils.channel();
        original.setId(1);
        Channel updated = EntityUtils.channel();;
        updated.setDescription(channelUpdatingDto.getDescription());
        updated.setRecipients(channelUpdatingDto.getRecipients());
        updated.setId(1);

        when(channelRepository.findById(1)).thenReturn(Optional.of(original));
        when(channelRepository.save(updated)).thenReturn(updated);

        Channel actual = channelService.updateChannel(1, channelUpdatingDto);

        verify(channelRepository, times(1)).findById(1);
        verify(channelRepository, times(1)).save(updated);
        assertEquals(updated, actual);
    }

    @Test
    void should_not_update_channel_with_not_found_id() {
        ChannelUpdatingDto channelUpdatingDto = EntityUtils.channelUpdatingDto();
        Channel updated = new Channel();
        updated.setDescription(channelUpdatingDto.getDescription());
        updated.setRecipients(channelUpdatingDto.getRecipients());
        updated.setId(1);

        when(channelRepository.findById(2)).thenReturn(Optional.empty());

        Channel actual = channelService.updateChannel(2, channelUpdatingDto);

        verify(channelRepository, times(1)).findById(2);
        verify(channelRepository, times(0)).save(updated);
        assertNull(actual);
    }

    @Test
    void should_get_channel_by_id() {
        Channel saved = EntityUtils.channel();
        saved.setId(1);

        when(channelRepository.findById(1)).thenReturn(Optional.of(saved));

        Channel actual = channelService.getChannelById(1);

        verify(channelRepository, times(1)).findById(1);
        assertEquals(saved, actual);
    }

    @Test
    void should_not_find_channel_by_id() {
        when(channelRepository.findById(1)).thenReturn(Optional.empty());

        Channel actual = channelService.getChannelById(1);

        Mockito.verify(channelRepository, times(1)).findById(1);
        assertNull(actual);
    }

    @Test
    void should_get_channels() {
        List<Channel> channels = EntityUtils.channels();

        when(channelRepository.findAll()).thenReturn(channels);

        List<Channel> actual = channelService.getChannels();

        verify(channelRepository, times(1)).findAll();
        assertEquals(channels, actual);
    }
}
