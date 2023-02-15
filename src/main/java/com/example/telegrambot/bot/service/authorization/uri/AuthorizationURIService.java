package com.example.telegrambot.bot.service.authorization.uri;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface AuthorizationURIService {
    SendMessage generateAuthorizationURI(Message message);

}
