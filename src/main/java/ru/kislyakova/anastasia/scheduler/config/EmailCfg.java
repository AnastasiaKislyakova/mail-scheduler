package ru.kislyakova.anastasia.scheduler.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class EmailCfg {
    @Value("${spring.mail.host}")
    private String host;
    @Value("${spring.mail.port}")
    private int port;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Value("${spring.mail.protocol}")
    private String protocol;
    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String transportProtocol;
    @Value("${spring.mail.properties.mail.smtp.auth")
    private String auth;
    @Value("${spring.mail.properties.mail.smtp.starttls.enable")
    private String starttlsEnable;
    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String sslTrust;
}
