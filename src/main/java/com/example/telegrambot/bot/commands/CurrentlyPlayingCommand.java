package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.playing.PlayingService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rainett.telegram.annotations.Command;
import org.rainett.telegram.annotations.Run;
import org.rainett.telegram.controller.executor.BotExecutor;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Slf4j
@Command(value = "/currently_playing")
public class CurrentlyPlayingCommand {

    private final PlayingService playingService;
    private final ExceptionHandler handler;
    private final BotExecutor bot;


    @TokenRefresh
    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        try {
            SendPhoto sendPhoto = playingService.prepareSendPhoto(message);
            bot.execute(sendPhoto);
        } catch (UserNotFoundException e) {
            handler.userNotFound(message, e);
        } catch (CurrentlyPlayingNotFoundException e) {
            handler.currentlyPlayingNotFound(message, e);
        } catch (UserNotListeningException e) {
            handler.userNotListening(message);
        }

    }

}
