package com.example.telegrambot.bot.service.authorization;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

public interface AuthorizationService {

    String authorize(Update update);

}
