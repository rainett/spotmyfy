package com.example.telegrambot.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

@Slf4j
@Component
public class BotInitializer {

    final List<Bot> bots;

    public BotInitializer(List<Bot> bots) {
        this.bots = bots;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() throws TelegramApiException {
        log.info("Initializing bots");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        bots.forEach(bot -> {
            try {
                telegramBotsApi.registerBot(bot);
            } catch (TelegramApiException e) {
                log.error("Error occurred: " + e.getMessage());
            }
        });
        log.info("Initializing finished successfully");
    }

}
