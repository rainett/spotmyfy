package com.example.telegrambot.telegram.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class BotConfig {

    @Value("${bot.path}")
    private String webhookPath;

    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

}
