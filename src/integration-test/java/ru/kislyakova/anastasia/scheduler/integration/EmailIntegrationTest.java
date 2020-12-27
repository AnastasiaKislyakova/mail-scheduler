package ru.kislyakova.anastasia.scheduler.integration;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kislyakova.anastasia.scheduler.controller.EmailController;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.service.EmailService;

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
class EmailIntegrationTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private EmailService emailService;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "test"))
            .withPerMethodLifecycle(false);

    @Test
    void should_create_and_send_email() throws MessagingException {
        EmailCreationDto emailDto = new EmailCreationDto(1, 1, "ab@gmail.com",
                "Subject A", "Text A");

        webClient.post()
                .uri("/api/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(emailDto))
                .exchange()
                .expectStatus().isOk();

        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        assertEquals("Subject A", receivedMessage.getSubject());
        assertEquals("Text A", GreenMailUtil.getBody(receivedMessage));
        assertEquals(1, receivedMessage.getAllRecipients().length);
        assertEquals("ab@gmail.com", receivedMessage.getAllRecipients()[0].toString());
    }

    @Test
    void should_get_email_by_id() {
        EmailCreationDto emailDto = new EmailCreationDto(1, 1, "ab@gmail.com",
                "Subject A", "Text A");
        Email email = emailService.sendEmail(emailDto);
        int id = 1;

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
    }

    @Test
    void should_not_find_email_by_id() {
        int id = 2;

        webClient.get()
                .uri("/api/emails/{emailId}", id)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody().isEmpty();
    }

    @Test
    void should_get_emails() {
        EmailCreationDto emailDto1 = new EmailCreationDto(1, 1, "ab@gmail.com",
                "Subject A", "Text A");
        Email email1 = emailService.sendEmail(emailDto1);

        EmailCreationDto emailDto2 = new EmailCreationDto(1, 1, "abc@gmail.com",
                "Subject A", "Text A");
        Email email2 = emailService.sendEmail(emailDto2);

        List<Email> emails = Arrays.asList(email1, email2);

        webClient.get()
                .uri("/api/emails")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Email.class).value(elements -> {
            MatcherAssert.assertThat(elements, Matchers.hasSize(2));
            MatcherAssert.assertThat(elements, Matchers.hasItem(emails.get(0)));
            MatcherAssert.assertThat(elements, Matchers.hasItem(emails.get(1)));
        });
    }


}