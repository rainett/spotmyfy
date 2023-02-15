package com.example.telegrambot.bot.service.authorization;

import com.example.telegrambot.spotify.exceptions.AuthorizationCodeNotFound;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface StartService {

    SendMessage start(Message message) throws AuthorizationFailedException, AuthorizationCodeNotFound;

}
