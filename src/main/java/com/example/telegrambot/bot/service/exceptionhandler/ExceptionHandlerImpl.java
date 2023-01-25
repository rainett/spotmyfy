package com.example.telegrambot.bot.service.exceptionhandler;

import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.exceptions.*;
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
    private final MessageService messageService;

    @Override
    public void userNotFound(String chatId, Long userId, UserNotFoundException e) {
        log.error("User was not found", e);
        bot.execute(new SendMessage(chatId,
                messageService.getMessage("exception.user_not_found", userId)));
    }

    @Override
    public void currentlyPlayingNotFound(String chatId, Long userId, CurrentlyPlayingNotFoundException e) {
        log.error("Failed to get user's currently playing track", e);
        bot.execute(new SendMessage(chatId,
                messageService.getMessage("exception.currently_playing_not_found", userId)));
    }

    @Override
    public void userNotListening(String chatId, Long userId) {
        bot.execute(new SendMessage(chatId,
                messageService.getMessage("exception.user_not_listening", userId)));
    }

    @Override
    public void topTracks(String chatId, Long userId, TopTracksException e) {
        log.error("Failed to get user's top tracks", e);
        bot.execute(new SendMessage(chatId,
                messageService.getMessage("exception.top_tracks", userId)));
    }

    @Override
    public void authorizationFailed(String chatId, Long userId, AuthorizationFailedException e) {
        log.error("Failed to perform authorization", e);
        bot.execute(new SendMessage(chatId,
                messageService.getMessage("exception.authorization_failed", userId)));
    }

    @Override
    public void authorizationCodeNotFound(String chatId, Long userId, AuthorizationCodeNotFound e) {
        log.error("Failed to perform authorization", e);
        bot.execute(new SendMessage(chatId,
                messageService.getMessage("exception.authorization_code_not_found", userId)));
    }
}
