package com.example.telegrambot.bot.service.exceptionhandler;

import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;

public interface ExceptionHandler {
    void userNotFound(String chatId, UserNotFoundException e);

    void currentlyPlayingNotFound(String chatId, CurrentlyPlayingNotFoundException e);

    void userNotListening(String chatId);

    void topTracks(String chatId, TopTracksException e);

    void authorizationFailed(String toString, AuthorizationFailedException e);
}
