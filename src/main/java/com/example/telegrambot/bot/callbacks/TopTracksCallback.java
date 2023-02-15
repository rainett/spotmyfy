package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams.TrackMessage;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.bot.service.toptracks.callback.TopTracksCallbackService;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.rainett.javagram.annotations.Callback;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.async.BotExecutorAsync;
import com.rainett.javagram.keyboard.ButtonCallback;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

@RequiredArgsConstructor
@Callback(value = "top_tracks", fromSender = true)
@Slf4j
public class TopTracksCallback {

    private final ExceptionHandler handler;
    private final TopTracksCallbackService service;
    private final BotExecutorAsync bot;


    @TokenRefresh
    @Run
    public void run(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        Long userId = callbackQuery.getFrom().getId();
        ButtonCallback buttonCallback = new ButtonCallback(callbackQuery);
        TopTracksCallbackParams params = new TopTracksCallbackParams(buttonCallback.getParameters());
        Paging<Track> trackPage;
        try {
            trackPage = service.getTrackPage(userId, params);
        } catch (UserNotFoundException e) {
            handler.userNotFoundCallback(callbackQuery, e);
            return;
        } catch (TopTracksException e) {
            handler.topTracksCallback(callbackQuery, e);
            return;
        }
        if (trackPage.getItems().length == 0) {
            AnswerCallbackQuery answer = service.getNoTracksMessage(callbackQuery);
            bot.execute(answer);
            return;
        }
        TrackMessage trackMessage = params.getTrackMessage();
        if (params.hasTrackMessage()) {
            List<EditMessageMedia> editMediaList = service.getEditMedias(trackPage, trackMessage, callbackQuery);
            editMediaList.forEach(bot::execute);
        } else {
            SendMediaGroup sendMediaGroup = service.getMediaGroup(trackPage, callbackQuery);
            List<Message> messages = bot.execute(sendMediaGroup).join();
            trackMessage = new TrackMessage(messages.get(0).getMessageId(), messages.size());
        }
        EditMessageText editMessageText = service.getEditText(update, trackPage, trackMessage);
        bot.execute(editMessageText);
    }

}
