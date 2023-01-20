package com.example.telegrambot.bot.service.authorization;

import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface AuthorizationService {

    String authorize(Update update) throws AuthorizationFailedException;

}
