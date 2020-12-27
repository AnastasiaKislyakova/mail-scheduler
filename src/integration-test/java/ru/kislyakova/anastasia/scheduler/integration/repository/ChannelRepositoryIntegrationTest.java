package ru.kislyakova.anastasia.scheduler.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@ActiveProfiles("integration-test")
@SpringBootTest
public class ChannelRepositoryIntegrationTest {

    @Autowired
    private ChannelRepository channelRepository;

    @Test
    void should_not_save_channel_with_existing_name() {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        Channel channel1 = new Channel(channelDto);
        Channel channel2 = new Channel(channelDto);
        channelRepository.save(channel1);
        assertThrows(DataIntegrityViolationException.class, () -> channelRepository.save(channel2));
    }

    void should_find_all() {

    }

    void should_find_channel_by_name() {

    }
}
