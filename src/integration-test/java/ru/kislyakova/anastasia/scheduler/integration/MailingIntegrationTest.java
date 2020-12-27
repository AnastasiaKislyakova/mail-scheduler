package ru.kislyakova.anastasia.scheduler.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kislyakova.anastasia.scheduler.controller.MailingController;
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;
import ru.kislyakova.anastasia.scheduler.service.MailingService;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
class MailingIntegrationTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChannelRepository channelRepository;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Test
    void should_create_mailing() {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        channelService.createChannel(channelDto);
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");

        webClient.post()
                .uri("/api/mailings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mailingDto))
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void should_send_mailing() throws MessagingException {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        channelService.createChannel(channelDto);
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing = mailingService.createMailing(mailingDto);
        int id = 1;

        webClient.post()
                .uri("/api/mailings/{mailingId}/send", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.channelId").isEqualTo(mailing.getChannelId())
                .jsonPath("$.subject").isEqualTo(mailing.getSubject())
                .jsonPath("$.text").isEqualTo(mailing.getText())
                .jsonPath("$.attempt").isEqualTo(mailing.getAttempt() + 1);

        assertEquals(2, greenMail.getReceivedMessages().length);

        MimeMessage receivedMessage1 = greenMail.getReceivedMessages()[0];
        assertEquals("Subject A", receivedMessage1.getSubject());
        assertEquals("Text A", GreenMailUtil.getBody(receivedMessage1));
        assertEquals(1, receivedMessage1.getAllRecipients().length);
        assertEquals("ab@gmail.com", receivedMessage1.getAllRecipients()[0].toString());

        MimeMessage receivedMessage2 = greenMail.getReceivedMessages()[1];
        assertEquals("Subject A", receivedMessage2.getSubject());
        assertEquals("Text A", GreenMailUtil.getBody(receivedMessage2));
        assertEquals(1, receivedMessage2.getAllRecipients().length);
        assertEquals("abc@gmail.com", receivedMessage2.getAllRecipients()[0].toString());
    }

    @Test
    void should_not_send_not_found_by_id_mailing() {
        int id = 1;

        webClient.post()
                .uri("/api/mailings/{mailingId}/send", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    //@Test
    void should_not_send_mailing_with_not_found_channel() {
        //TODO check sending mailing with not found channel?
        int id = 1;
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        Channel channel = channelService.createChannel(channelDto);
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing = mailingService.createMailing(mailingDto);

        //can't delete channel because of foreign key
        channelRepository.delete(channel);

        webClient.post()
                .uri("/api/mailings/{mailingId}/send", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void should_get_mailing_by_id() {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        channelService.createChannel(channelDto);
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing = mailingService.createMailing(mailingDto);
        int id = 1;

        webClient.get()
                .uri("/api/mailings/{mailingId}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.channelId").isEqualTo(mailing.getChannelId())
                .jsonPath("$.subject").isEqualTo(mailing.getSubject())
                .jsonPath("$.text").isEqualTo(mailing.getText())
                .jsonPath("$.attempt").isEqualTo(mailing.getAttempt());

    }

    @Test
    void should_not_find_mailing_by_id() {
        int id = 2;

        webClient.get()
                .uri("/api/mailings/{mailingId}", id)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void should_get_mailings() {
        ChannelCreationDto channelDto = new ChannelCreationDto("A", "channel A",
                Arrays.asList("ab@gmail.com", "abc@gmail.com"));
        channelService.createChannel(channelDto);

        MailingCreationDto mailingDto1 = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing1 = mailingService.createMailing(mailingDto1);

        MailingCreationDto mailingDto2 = new MailingCreationDto(1, "Subject B", "Text B");
        Mailing mailing2 = mailingService.createMailing(mailingDto2);

        List<Mailing> mailings = Arrays.asList(mailing1, mailing2);

        webClient.get()
                .uri("/api/mailings")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Mailing.class).value(elements -> {
            MatcherAssert.assertThat(elements, Matchers.hasSize(2));
            MatcherAssert.assertThat(elements, Matchers.hasItem(mailings.get(0)));
            MatcherAssert.assertThat(elements, Matchers.hasItem(mailings.get(1)));
        });
    }
}