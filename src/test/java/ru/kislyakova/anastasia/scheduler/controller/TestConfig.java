package ru.kislyakova.anastasia.scheduler.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.repository.EmailRepository;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;

@TestConfiguration
public class TestConfig {

    @MockBean
    ChannelRepository channelRepository;

    @MockBean
    EmailRepository emailRepository;

    @MockBean
    MailingRepository mailingRepository;
}

