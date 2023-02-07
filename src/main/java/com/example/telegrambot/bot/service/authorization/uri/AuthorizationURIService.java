package com.example.telegrambot.bot.service.authorization.uri;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface AuthorizationURIService {
    SendMessage generateAuthorizationURI(String chatId, Long userId, Integer messageId);
}
