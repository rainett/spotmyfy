package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.analyzecurrent.AnalyzeCurrentService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.AudioFeaturesNotFoundException;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import com.rainett.javagram.annotations.Command;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.async.BotExecutorAsync;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command(value = "/analyze_current", description = "Analyzes currently playing track")
public class AnalyzeCurrentCommand {

    private final AnalyzeCurrentService analyzeCurrentService;
    private final ExceptionHandler exceptionHandler;
    private final BotExecutorAsync bot;

    @TokenRefresh
    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        try {
            SendPhoto sendPhoto = analyzeCurrentService.getCurrentAnalysis(message);
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
