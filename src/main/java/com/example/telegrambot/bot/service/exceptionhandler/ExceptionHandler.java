package com.example.telegrambot.bot.service.exceptionhandler;

import com.example.telegrambot.spotify.exceptions.*;

public interface ExceptionHandler {
    void userNotFound(String chatId, Long userId, UserNotFoundException e);

    void currentlyPlayingNotFound(String chatId, Long userId, CurrentlyPlayingNotFoundException e);

    void userNotListening(String chatId, Long userId);

    void topTracks(String chatId, Long userId, TopTracksException e);

    void authorizationFailed(String chatId, Long userId, AuthorizationFailedException e);

    void authorizationCodeNotFound(String chatId, Long userId, AuthorizationCodeNotFound e);
}
