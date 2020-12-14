package ru.kislyakova.anastasia.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.kislyakova.anastasia.scheduler.repository.ChannelRepository;
import ru.kislyakova.anastasia.scheduler.repository.MailingRepository;

import java.util.Locale;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class SchedulerApplication implements CommandLineRunner {

	private final ChannelRepository channelRepository;

	private final MailingRepository mailingRepository;

	public SchedulerApplication(ChannelRepository channelRepository, MailingRepository mailingRepository) {
		this.channelRepository = channelRepository;
		this.mailingRepository = mailingRepository;
	}

	@Override
	public void run(String... args) throws Exception {
		TeaTimeMailing.create(channelRepository, mailingRepository);
	}

	public static void main(String[] args) {
		Locale.setDefault(Locale.US);
		SpringApplication.run(SchedulerApplication.class, args);
	}

}
