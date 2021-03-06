package ru.kislyakova.anastasia.scheduler.controller;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.exception.DuplicateChannelException;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;
import ru.kislyakova.anastasia.scheduler.utils.EntityUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Import(TestConfig.class)
@WebFluxTest(ChannelController.class)
@ExtendWith(SpringExtension.class)
class ChannelControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private ChannelService channelService;

    @Test
    void should_create_channel() {
        ChannelCreationDto channelDto = EntityUtils.channelCreationDto();
        Channel channel = new Channel(channelDto);
        channel.setId(1);
        when(channelService.createChannel(channelDto)).thenReturn(channel);

        webClient.post()
                .uri("/api/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isCreated();

        verify(channelService, times(1)).createChannel(channelDto);
    }

    @Test
    void should_not_create_channel_with_existing_name() throws Exception {
        ChannelCreationDto channelDto = EntityUtils.channelCreationDto();
        Channel channel = new Channel(channelDto);
        DuplicateChannelException ex = new DuplicateChannelException(channel.getName());
        when(channelService.createChannel(channelDto)).thenThrow(ex);

        webClient.post()
                .uri("/api/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);

        verify(channelService, times(1)).createChannel(channelDto);
    }

    @Test
    void should_update_channel() {
        ChannelUpdatingDto channelDto = EntityUtils.channelUpdatingDto();
        ChannelCreationDto channelCreationDto = EntityUtils.channelCreationDto();
        Channel channel = new Channel(channelCreationDto);
        int id = 1;
        channel.setId(id);

        when(channelService.updateChannel(id, channelDto)).thenReturn(channel);

        webClient.put()
                .uri("/api/channels/{channelId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isOk();

        verify(channelService, times(1)).updateChannel(id, channelDto);
    }

    @Test
    void should_not_update_channel_with_not_found_id() {
        ChannelUpdatingDto channelDto = EntityUtils.channelUpdatingDto();
        ChannelCreationDto channelCreationDto = EntityUtils.channelCreationDto();
        Channel channel = new Channel(channelCreationDto);
        int id = 1;
        channel.setId(id);

        when(channelService.updateChannel(id, channelDto)).thenReturn(null);

        webClient.put()
                .uri("/api/channels/{channelId}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isNotFound();

        verify(channelService, times(1)).updateChannel(id, channelDto);
    }

    @Test
    void should_get_channel_by_id() {
        Channel channel = EntityUtils.channel();
        List<String> recipients = channel.getRecipients();
        int id = 1;
        channel.setId(id);

        when(channelService.getChannelById(id)).thenReturn(channel);

        webClient.get()
                .uri("/api/channels/{channelId}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.name").isEqualTo(channel.getName())
                .jsonPath("$.description").isEqualTo(channel.getDescription())
                .jsonPath("$.recipients", Matchers.hasSize(recipients.size())).isArray()
                .jsonPath("$.recipients[0]").isEqualTo(recipients.get(0))
                .jsonPath("$.recipients[1]").isEqualTo(recipients.get(1));

        verify(channelService, times(1)).getChannelById(id);
    }

    @Test
    void should_not_find_channel_by_id() {
        int id = 2;

        when(channelService.getChannelById(id)).thenReturn(null);

        webClient.get()
                .uri("/api/channels/{channelId}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(channelService, times(1)).getChannelById(id);
    }

    @Test
    void should_get_channels() {
        List<Channel> channels = EntityUtils.channels();
        when(channelService.getChannels()).thenReturn(channels);

        webClient.get()
                .uri("/api/channels")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Channel.class).value(elements -> {
            MatcherAssert.assertThat(elements, Matchers.hasSize(2));
            MatcherAssert.assertThat(elements, Matchers.hasItem(channels.get(0)));
            MatcherAssert.assertThat(elements, Matchers.hasItem(channels.get(1)));
        });

        verify(channelService, times(1)).getChannels();
    }

}