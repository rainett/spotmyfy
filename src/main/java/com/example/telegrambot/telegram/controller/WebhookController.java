package com.example.telegrambot.telegram.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@Slf4j
public class WebhookController {

    private final WebhookBot bot;

    public WebhookController(WebhookBot bot) {
        this.bot = bot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        log.info("Received an update, {}", update.toString().replaceAll("\\w+=null,? ?", ""));
        return bot.onWebhookUpdateReceived(update);
    }

}
