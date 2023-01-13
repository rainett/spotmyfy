package com.example.telegrambot.telegram.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Slf4j
@RestController
public class WebhookController {

    private final UpdateProcessor processor;

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        log.info("Received an update, {}", update.toString().replaceAll("\\w+=null,? ?", ""));
        return processor.onWebhookUpdateReceived(update);
    }

}

