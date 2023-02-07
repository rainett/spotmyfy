package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.analyzecurrent.AnalyzeCurrentService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.AudioFeaturesNotFoundException;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command("/analyze_current")
public class AnalyzeCurrentCommand {

    private final AnalyzeCurrentService analyzeCurrentService;
    private final ExceptionHandler exceptionHandler;
    private final BotExecutor bot;

    @TokenRefresh
    @Runnable
    public void run(Update update) {
        Message message = update.getMessage();
        try {
            SendPhoto sendPhoto = analyzeCurrentService.getCurrentAnalyze(update.getMessage());
            bot.execute(sendPhoto);
        } catch (UserNotFoundException e) {
            exceptionHandler.userNotFound(message, e);
        } catch (CurrentlyPlayingNotFoundException e) {
            exceptionHandler.currentlyPlayingNotFound(message, e);
        } catch (UserNotListeningException e) {
            exceptionHandler.userNotListening(message);
        } catch (AudioFeaturesNotFoundException e) {
            exceptionHandler.audioFeaturesNotFound(message, e);
        }
    }

}
