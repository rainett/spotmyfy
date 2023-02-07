package com.example.telegrambot.bot.service.currentlyplaying;

import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;

public interface CurrentlyPlayingService {

    SimplifiedTrack getCurrentlyPlayingTrack(Long userId) throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException;

}
