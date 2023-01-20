package com.example.telegrambot.bot.service.exceptionhandler;

import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExceptionHandlerImpl implements ExceptionHandler {

    private final BotExecutor bot;

    @Override
    public void userNotFound(String chatId, UserNotFoundException e) {
        log.error("User was not found", e);
        bot.execute(new SendMessage(chatId, "Your account was not found, try using /spotify command"));
    }

    @Override
    public void currentlyPlayingNotFound(String chatId, CurrentlyPlayingNotFoundException e) {
        log.error("Failed to get user's currently playing track", e);
        bot.execute(new SendMessage(chatId, "Failed to get your track, try using /spotify command"));
    }

    @Override
    public void userNotListening(String chatId) {
        bot.execute(new SendMessage(chatId, "Where are your headphones? Your ass is not listening to any song!"));
    }

    @Override
    public void topTracks(String chatId, TopTracksException e) {
        log.error("Failed to get user's top tracks", e);
        bot.execute(new SendMessage(chatId, "Failed to get your top tracks, try using /spotify command"));
    }

    @Override
    public void authorizationFailed(String chatId, AuthorizationFailedException e) {
        log.error("Failed to perform authorization", e);
        bot.execute(new SendMessage(chatId, "Failed performing authorization. Try again /spotify"));
    }
}
