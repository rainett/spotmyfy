package com.example.telegrambot.bot.commands.currentlyplaying;

import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class CurrentlyPLayingExceptionHandlerImpl implements CurrentlyPlayingExceptionHandler {

    private final BotExecutor bot;

    @Override
    public SimplifiedTrack userNotFound(String chatId, UserNotFoundException e) {
        log.error("User was not found", e);
        bot.execute(new SendMessage(chatId, "Your account was not found, try using /spotify command"));
        return null;
    }

    @Override
    public SimplifiedTrack currentlyPlayingNotFound(String chatId, CurrentlyPlayingNotFoundException e) {
        log.error("Failed to get user's currently playing track", e);
        bot.execute(new SendMessage(chatId, "Failed to get your track, try using /spotify command"));
        return null;
    }

    @Override
    public SimplifiedTrack userNotListening(String chatId) {
        bot.execute(new SendMessage(chatId, "Where are your headphones? Your ass is not listening to any song!"));
        return null;
    }
}
