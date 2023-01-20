package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.currentlyplaying.CurrentlyPlayingService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Slf4j
@Command(name = "/currently_playing")
public class CurrentlyPlayingCommand {

    private final CurrentlyPlayingService currentlyPlayingService;
    private final ExceptionHandler handler;
    private final BotExecutor bot;


    @TokenRefresh
    @Runnable
    public void run(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        try {
            SendPhoto sendPhoto = currentlyPlayingService.prepareSendPhoto(message);
            bot.execute(sendPhoto);
        } catch (UserNotFoundException e) {
            handler.userNotFound(chatId, e);
        } catch (CurrentlyPlayingNotFoundException e) {
            handler.currentlyPlayingNotFound(chatId, e);
        } catch (UserNotListeningException e) {
            handler.userNotListening(chatId);
        }

    }

}
