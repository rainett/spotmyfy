package com.example.telegrambot.bot.service.exceptionhandler;

import com.example.telegrambot.spotify.exceptions.*;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface ExceptionHandler {

    void userNotFound(Message message, UserNotFoundException e);

    void userNotFoundCallback(CallbackQuery callbackQuery, UserNotFoundException e);

    void currentlyPlayingNotFound(Message message, CurrentlyPlayingNotFoundException e);

    void userNotListening(Message message);

    void topTracksCallback(CallbackQuery callbackQuery, TopTracksException e);

    void authorizationCodeNotFound(Message message, AuthorizationCodeNotFound e);

    void authorizationFailed(Message message, AuthorizationFailedException e);

    void audioFeaturesNotFound(Message message, AudioFeaturesNotFoundException e);
}
