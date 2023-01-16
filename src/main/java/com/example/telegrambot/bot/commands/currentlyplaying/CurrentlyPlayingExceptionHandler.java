package com.example.telegrambot.bot.commands.currentlyplaying;

import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;

public interface CurrentlyPlayingExceptionHandler {
    SimplifiedTrack userNotFound(String chatId, UserNotFoundException e);

    SimplifiedTrack currentlyPlayingNotFound(String chatId, CurrentlyPlayingNotFoundException e);

    SimplifiedTrack userNotListening(String chatId);
}
