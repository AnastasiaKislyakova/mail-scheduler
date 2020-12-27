package ru.kislyakova.anastasia.scheduler.integration;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.ChannelUpdatingDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;

import java.util.Arrays;
import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class ChannelIntegrationTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ChannelService channelService;

    @Test
    void should_create_channel() {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));

        webClient.post()
                .uri("/api/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void should_not_create_channel_with_existing_name() throws Exception {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        Channel channel = channelService.createChannel(channelDto);

        webClient.post()
                .uri("/api/channels")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void should_update_channel() {
        ChannelUpdatingDto channelDto = new ChannelUpdatingDto("channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com", "abcd@gmail.com"));
        ChannelCreationDto channelCreationDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        channelService.createChannel(channelCreationDto);

        webClient.put()
                .uri("/api/channels/{channelId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isOk();

    }

    @Test
    void should_not_update_channel_with_not_found_id() {
        ChannelUpdatingDto channelDto = new ChannelUpdatingDto("channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));

        webClient.put()
                .uri("/api/channels/{channelId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(channelDto))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void should_get_channel_by_id() {
        List<String> recipients = Arrays.asList("ab@gmail.com", "abc@gmail.com");
        ChannelCreationDto channelCreationDto = new ChannelCreationDto("A", "channel A", recipients);
        int id = 1;
        Channel channel = new Channel(channelCreationDto);
        channelService.createChannel(channelCreationDto);

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
    }

    @Test
    void should_not_find_channel_by_id() {
        int id = 2;

        webClient.get()
                .uri("/api/channels/{channelId}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void should_get_channels() {
        List<String> recipients = Arrays.asList("ab@gmail.com", "abc@gmail.com");
        ChannelCreationDto channelCreationDto1 = new ChannelCreationDto("A", "channel A", recipients);
        Channel channel1 = channelService.createChannel(channelCreationDto1);
        ChannelCreationDto channelCreationDto2 = new ChannelCreationDto("B", "channel B", recipients);
        Channel channel2 = channelService.createChannel(channelCreationDto2);

        List<Channel> channels = Arrays.asList(channel1, channel2);

        webClient.get()
                .uri("/api/channels")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Channel.class).value(elements -> {
            MatcherAssert.assertThat(elements, Matchers.hasSize(2));
            MatcherAssert.assertThat(elements, Matchers.hasItem(channels.get(0)));
            MatcherAssert.assertThat(elements, Matchers.hasItem(channels.get(1)));
        });

    }

}