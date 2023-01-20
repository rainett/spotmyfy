package com.example.telegrambot.bot.service.currentlyplaying;

import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CurrentlyPlayingService {
    SendPhoto prepareSendPhoto(Message message)  throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException;
}
