package com.example.telegrambot.bot.commands.currentlyplaying;

import com.example.telegrambot.bot.service.currentlyplaying.CurrentlyPlayingService;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Slf4j
@Command(name = "/currently_playing")
public class CurrentlyPlayingCommand {

    private final CurrentlyPlayingService currentlyPlayingService;
    private final CurrentlyPlayingExceptionHandler handler;
    private final BotExecutor bot;


    @Runnable
    public void run(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        SimplifiedTrack currentlyPlaying = getSimplifiedTrack(userId, chatId);
        if (currentlyPlaying == null) return;
        SendPhoto sendPhoto = getSendPhoto(currentlyPlaying, chatId);
        bot.execute(sendPhoto);
    }

    private SendPhoto getSendPhoto(SimplifiedTrack currentlyPlaying, String chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(currentlyPlaying.getImageUrl()));
        sendPhoto.setParseMode("HTML");
        sendPhoto.setCaption(currentlyPlaying.toTextMessage());
        return sendPhoto;
    }

    private SimplifiedTrack getSimplifiedTrack(Long userId, String chatId) {
        SimplifiedTrack currentlyPlaying;
        try {
            currentlyPlaying = currentlyPlayingService.getCurrentlyPlayingTrack(userId);
        } catch (UserNotFoundException e) {
            return handler.userNotFound(chatId, e);
        } catch (CurrentlyPlayingNotFoundException e) {
            return handler.currentlyPlayingNotFound(chatId, e);
        } catch (UserNotListeningException e) {
            return handler.userNotListening(chatId);
        }
        return currentlyPlaying;
    }


}
