package com.example.telegrambot.bot.service.toptracks.callback;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams.TrackMessage;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.util.List;

public interface TopTracksCallbackService {

    Paging<Track> getTrackPage(Long userId, TopTracksCallbackParams params) throws UserNotFoundException, TopTracksException;

    List<EditMessageMedia> getEditMedias(Paging<Track> trackPage, TrackMessage trackMessage, CallbackQuery chatId);

    SendMediaGroup getMediaGroup(Paging<Track> trackPage, CallbackQuery chatId);

    EditMessageText getEditText(Update update, Paging<Track> trackPage, TrackMessage trackMessage);

    AnswerCallbackQuery getNoTracksMessage(CallbackQuery callbackQuery);
}
