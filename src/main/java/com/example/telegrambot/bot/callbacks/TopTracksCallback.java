package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams.TrackMessage;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.bot.service.toptracks.callback.TopTracksCallbackService;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

@RequiredArgsConstructor
@Callback(callbackName = "top_tracks", fromSender = true)
@Slf4j
public class TopTracksCallback {

    private final ExceptionHandler handler;
    private final TopTracksCallbackService service;
    private final BotExecutor bot;


    @TokenRefresh
    @Runnable
    public void run(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        ButtonCallback buttonCallback = new ButtonCallback(update);
        TopTracksCallbackParams params = new TopTracksCallbackParams(buttonCallback.getParameters());
        Paging<Track> trackPage;
        try {
            Long userId = update.getCallbackQuery().getFrom().getId();
            trackPage = service.getTrackPage(userId, params);
        } catch (UserNotFoundException e) {
            handler.userNotFound(chatId.toString(), e);
            return;
        } catch (TopTracksException e) {
            handler.topTracks(chatId.toString(), e);
            return;
        }
        TrackMessage trackMessage = params.getTrackMessage();
        if (params.hasTrackMessage()) {
            List<EditMessageMedia> editMediaList = service.getEditMedias(trackPage, trackMessage, chatId);
            editMediaList.forEach(bot::execute);
        } else {
            SendMediaGroup sendMediaGroup = service.getMediaGroup(trackPage, chatId);
            List<Message> messages = bot.execute(sendMediaGroup);
            trackMessage = new TrackMessage(messages.get(0).getMessageId(), messages.size());
        }
        EditMessageText editMessageText = service.getEditText(update, trackPage, trackMessage);
        bot.execute(editMessageText);
    }

}