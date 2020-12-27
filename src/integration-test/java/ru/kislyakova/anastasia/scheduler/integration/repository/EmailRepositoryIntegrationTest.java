package ru.kislyakova.anastasia.scheduler.integration.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.kislyakova.anastasia.scheduler.dto.EmailCreationDto;
import ru.kislyakova.anastasia.scheduler.entity.Email;
import ru.kislyakova.anastasia.scheduler.repository.EmailRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Testcontainers
@ActiveProfiles("integration-test")
@SpringBootTest
public class EmailRepositoryIntegrationTest {
    @Autowired
    private EmailRepository emailRepository;

    @Test
    void should_not_save_channel_with_existing_name() {
        EmailCreationDto emailDto = new EmailCreationDto(1, 1, "ab@gmail.com",
                "Subject A", "Text A");
        Email email1 = new Email(emailDto);
        emailRepository.save(email1);

        Email email2 = new Email(emailDto);

        assertThrows(DataIntegrityViolationException.class, () -> emailRepository.save(email2));
    }


}
