package com.example.telegrambot.bot.service.playing;

import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface PlayingService {
    SendPhoto prepareSendPhoto(Message message)  throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException;
}
