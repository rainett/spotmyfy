package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.analyzecurrent.AnalyzeCurrentService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.AudioFeaturesNotFoundException;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import lombok.RequiredArgsConstructor;
import org.rainett.telegram.annotations.Command;
import org.rainett.telegram.annotations.Run;
import org.rainett.telegram.controller.executor.BotExecutor;
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
    @Run
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
