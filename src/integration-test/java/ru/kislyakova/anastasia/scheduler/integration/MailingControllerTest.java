package ru.kislyakova.anastasia.scheduler.integration;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.kislyakova.anastasia.scheduler.controller.MailingController;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.service.MailingService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@WebFluxTest(MailingController.class)
@ExtendWith(SpringExtension.class)
class MailingControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MailingService mailingService;

    @Test
    void should_create_mailing() {
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing = new Mailing(mailingDto);
        mailing.setId(1);

        when(mailingService.createMailing(mailingDto)).thenReturn(mailing);

        webClient.post()
                .uri("/api/mailings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mailingDto))
                .exchange()
                .expectStatus().isCreated();

        Mockito.verify(mailingService, times(1)).createMailing(mailingDto);
    }

    @Test
    void should_send_mailing() {
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing = new Mailing(mailingDto);
        int id = 1;
        mailing.setId(id);
        when(mailingService.sendMailing(id)).thenReturn(mailing);

        webClient.post()
                .uri("/api/mailings/{mailingId}/send", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.channelId").isEqualTo(mailing.getChannelId())
                .jsonPath("$.subject").isEqualTo(mailing.getSubject())
                .jsonPath("$.text").isEqualTo(mailing.getText())
                .jsonPath("$.attempt").isEqualTo(mailing.getAttempt());

        Mockito.verify(mailingService, times(1)).sendMailing(id);
    }

    @Test
    void should_not_send_not_found_by_id_mailing() {
        int id = 2;

        when(mailingService.sendMailing(id)).thenReturn(null);

        webClient.post()
                .uri("/api/mailings/{mailingId}/send", id)
                .exchange()
                .expectStatus().isNotFound();

        Mockito.verify(mailingService, times(1)).sendMailing(id);
    }

    @Test
    void should_get_mailing_by_id() {
        MailingCreationDto mailingDto = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing = new Mailing(mailingDto);
        int id = 1;
        mailing.setId(id);

        when(mailingService.getMailingById(id)).thenReturn(mailing);

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

        Mockito.verify(mailingService, times(1)).getMailingById(id);
    }

    @Test
    void should_not_find_mailing_by_id() {
        int id = 2;

        when(mailingService.sendMailing(id)).thenReturn(null);

        webClient.get()
                .uri("/api/mailings/{mailingId}", id)
                .exchange()
                .expectStatus().isNotFound();

        Mockito.verify(mailingService, times(1)).getMailingById(id);
    }

    @Test
    void should_get_mailings() {
        MailingCreationDto mailingDto1 = new MailingCreationDto(1, "Subject A", "Text A");
        Mailing mailing1 = new Mailing(mailingDto1);
        mailing1.setId(1);

        MailingCreationDto mailingDto2 = new MailingCreationDto(1, "Subject B", "Text B");
        Mailing mailing2 = new Mailing(mailingDto2);
        mailing2.setId(2);

        List<Mailing> mailings = Arrays.asList(mailing1, mailing2);
        when(mailingService.getMailings()).thenReturn(mailings);

        webClient.get()
                .uri("/api/mailings")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Mailing.class).value(elements -> {
            MatcherAssert.assertThat(elements, Matchers.hasSize(2));
            MatcherAssert.assertThat(elements, Matchers.hasItem(mailings.get(0)));
            MatcherAssert.assertThat(elements, Matchers.hasItem(mailings.get(1)));
        });

        Mockito.verify(mailingService, times(1)).getMailings();
    }
}