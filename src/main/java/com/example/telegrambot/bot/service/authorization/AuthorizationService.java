package com.example.telegrambot.bot.service.authorization;

import com.example.telegrambot.spotify.exceptions.AuthorizationCodeNotFound;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface AuthorizationService {

    SendMessage authorize(Update update) throws AuthorizationFailedException, AuthorizationCodeNotFound;

}
