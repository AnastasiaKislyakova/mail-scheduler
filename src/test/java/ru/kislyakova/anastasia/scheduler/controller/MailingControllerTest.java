package ru.kislyakova.anastasia.scheduler.controller;

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
import ru.kislyakova.anastasia.scheduler.dto.ChannelCreationDto;
import ru.kislyakova.anastasia.scheduler.dto.MailingCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Channel;
import ru.kislyakova.anastasia.scheduler.entity.Mailing;
import ru.kislyakova.anastasia.scheduler.service.ChannelService;
import ru.kislyakova.anastasia.scheduler.service.MailingService;
import ru.kislyakova.anastasia.scheduler.utils.EntityUtils;

import javax.swing.text.html.parser.Entity;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Import(TestConfig.class)
@WebFluxTest(MailingController.class)
@ExtendWith(SpringExtension.class)
class MailingControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private MailingService mailingService;

    @Test
    void should_create_mailing() {
        MailingCreationDto mailingDto = EntityUtils.mailingCreationDto();
        Mailing mailing = new Mailing(mailingDto);
        mailing.setId(1);

        when(mailingService.createMailing(mailingDto)).thenReturn(mailing);

        webClient.post()
                .uri("/api/mailings")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(mailingDto))
                .exchange()
                .expectStatus().isCreated();

        verify(mailingService, times(1)).createMailing(mailingDto);
    }

    @Test
    void should_send_mailing() {
        Mailing mailing = EntityUtils.mailing();
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

        verify(mailingService, times(1)).sendMailing(id);
    }

    @Test
    void should_not_send_not_found_by_id_mailing() {
        int id = 2;

        when(mailingService.sendMailing(id)).thenReturn(null);

        webClient.post()
                .uri("/api/mailings/{mailingId}/send", id)
                .exchange()
                .expectStatus().isNotFound();

        verify(mailingService, times(1)).sendMailing(id);
    }

    @Test
    void should_get_mailing_by_id() {
        Mailing mailing = EntityUtils.mailing();
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

        verify(mailingService, times(1)).getMailingById(id);
    }

    @Test
    void should_not_find_mailing_by_id() {
        int id = 2;

        when(mailingService.sendMailing(id)).thenReturn(null);

        webClient.get()
                .uri("/api/mailings/{mailingId}", id)
                .exchange()
                .expectStatus().isNotFound();

        verify(mailingService, times(1)).getMailingById(id);
    }

    @Test
    void should_get_mailings() {
        List<Mailing> mailings = EntityUtils.mailings();
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

        verify(mailingService, times(1)).getMailings();
    }
}