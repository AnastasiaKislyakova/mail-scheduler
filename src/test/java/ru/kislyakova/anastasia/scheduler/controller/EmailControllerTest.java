package ru.kislyakova.anastasia.scheduler.controller;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.service.EmailService;
import ru.kislyakova.anastasia.scheduler.utils.EntityUtils;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@Import(TestConfig.class)
@WebFluxTest(EmailController.class)
@ExtendWith(SpringExtension.class)
class EmailControllerTest {
    @Autowired
    private WebTestClient webClient;

    @MockBean
    private EmailService emailService;

    @Test
    void should_create_and_send_email() {
        EmailCreationDto emailDto = EntityUtils.emailCreationDto();
        Email email = new Email(emailDto);
        email.setId(1);
        when(emailService.sendEmail(emailDto)).thenReturn(email);

        webClient.post()
                .uri("/api/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emailDto))
                .exchange()
                .expectStatus().isOk();

        verify(emailService, times(1)).sendEmail(emailDto);
    }

    @Test
    void should_get_email_by_id() {
        Email email = EntityUtils.email();
        int id = 1;
        email.setId(id);

        when(emailService.getEmailById(id)).thenReturn(email);

        webClient.get()
                .uri("/api/emails/{emailId}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(id)
                .jsonPath("$.mailingId").isEqualTo(email.getMailingId())
                .jsonPath("$.mailingAttempt").isEqualTo(email.getMailingAttempt())
                .jsonPath("$.recipient").isEqualTo(email.getRecipient())
                .jsonPath("$.subject").isEqualTo(email.getSubject())
                .jsonPath("$.text").isEqualTo(email.getText());

        verify(emailService, times(1)).getEmailById(id);
    }

    @Test
    void should_not_find_email_by_id() {
        int id = 2;

        when(emailService.getEmailById(id)).thenReturn(null);

        webClient.get()
                .uri("/api/emails/{emailId}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();

        verify(emailService, times(1)).getEmailById(id);
    }

    @Test
    void should_get_emails() {
        List<Email> emails = EntityUtils.emails();
        when(emailService.getEmails()).thenReturn(emails);

        webClient.get()
                .uri("/api/emails")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Email.class).value(elements -> {
            MatcherAssert.assertThat(elements, Matchers.hasSize(2));
            MatcherAssert.assertThat(elements, Matchers.hasItem(emails.get(0)));
            MatcherAssert.assertThat(elements, Matchers.hasItem(emails.get(1)));
        });

        verify(emailService, times(1)).getEmails();
    }


}