package com.example.telegrambot.bot.service.toptracks.callback;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams.TrackMessage;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardButton;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class TopTracksCallbackServiceImpl implements TopTracksCallbackService {

    private final UserRepository userRepository;
    private final SpotifyApiFactory spotifyApiFactory;


    @Override
    public Paging<Track> getTrackPage(Long userId, TopTracksCallbackParams params)
            throws UserNotFoundException, TopTracksException {
        String accessToken = userRepository.getByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = [" + userId + "] was not found"))
                .getAccessToken();
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromAccessToken(accessToken);
        try {
            return getTrackPaging(params, spotifyApi);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new TopTracksException(e);
        }
    }

    private Paging<Track> getTrackPaging(TopTracksCallbackParams params, SpotifyApi spotifyApi)
            throws IOException, SpotifyWebApiException, ParseException {
        return spotifyApi.getUsersTopTracks()
                .limit(params.getLimit())
                .time_range(params.getTimeRange().getCode())
                .offset(params.getOffset())
                .build()
                .execute();
    }

    @Override
    public List<EditMessageMedia> getEditMedias(Paging<Track> trackPage, TrackMessage trackMessage, Long chatId) {
        List<EditMessageMedia> editMediaList = new ArrayList<>();
        int messageId = trackMessage.trackMessageId();
        int limit = trackMessage.trackMessagesSize();
        StringBuilder stringBuilder = new StringBuilder();
        Track[] tracks = trackPage.getItems();
        for (int i = 0; i < limit; i++) {
            SimplifiedTrack track = new SimplifiedTrack(tracks[i]);
            EditMessageMedia e = new EditMessageMedia();
            e.setChatId(chatId);
            e.setMessageId(i + messageId);
            e.setMedia(new InputMediaPhoto(track.getImageUrl()));
            editMediaList.add(e);
            int place = trackPage.getOffset() + i + 1;
            stringBuilder.append(String.format("\n\n№%d ", place)).append(track.toTextMessage());
        }
        InputMedia firstMedia = editMediaList.get(0).getMedia();
        firstMedia.setParseMode("HTML");
        firstMedia.setCaption(stringBuilder.toString());
        return editMediaList;
    }

    @Override
    public SendMediaGroup getMediaGroup(Paging<Track> trackPage, Long chatId) {
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatId);
        List<InputMedia> medias = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        Track[] tracks = trackPage.getItems();
        for (int i = 0; i < trackPage.getLimit(); i++) {
            SimplifiedTrack track = new SimplifiedTrack(tracks[i]);
            InputMediaPhoto photo = new InputMediaPhoto();
            photo.setMedia(track.getImageUrl());
            medias.add(photo);
            int place = trackPage.getOffset() + i + 1;
            stringBuilder.append(String.format("\n\n№%d ", place)).append(track.toTextMessage());
        }
        InputMedia firstMedia = medias.get(0);
        firstMedia.setParseMode("HTML");
        firstMedia.setCaption(stringBuilder.toString());
        sendMediaGroup.setMedias(medias);
        return sendMediaGroup;
    }

    @Override
    public EditMessageText getEditText(Update update, Paging<Track> trackPage, TrackMessage trackMessage) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        ButtonCallback callback = new ButtonCallback(update);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        String text = getMessageText(trackPage, new TopTracksCallbackParams(callback.getParameters()));
        editMessageText.setText(text);

        List<MessageKeyboardButton> buttons = getButtons(trackPage, callback, trackMessage);
        MessageKeyboardRow row = new MessageKeyboardRow(buttons);
        MessageKeyboard messageKeyboard = new MessageKeyboard(row);
        InlineKeyboardMarkup replyMarkup = messageKeyboard.toInlineKeyboardMarkup();
        editMessageText.setReplyMarkup(replyMarkup);
        return editMessageText;
    }

    private List<MessageKeyboardButton> getButtons(Paging<Track> trackPage,
                                                   ButtonCallback oldCallback,
                                                   TrackMessage trackMessage) {
        List<MessageKeyboardButton> buttons = new ArrayList<>();
        if (trackPage.getPrevious() != null) {
            buttons.add(addPreviousButton(oldCallback, trackMessage));
        }
        if (trackPage.getNext() != null) {
            buttons.add(addNextButton(oldCallback, trackMessage));
        }

        return buttons;
    }

    private MessageKeyboardButton addNextButton(ButtonCallback oldCallback, TrackMessage trackMessage) {
        TopTracksCallbackParams params = new TopTracksCallbackParams(oldCallback.getParameters());
        params.setOffset(params.getOffset() + params.getLimit());
        params.setTrackMessage(trackMessage);
        String callbackName = oldCallback.getCallbackName();
        String messageSenderId = oldCallback.getMessageSenderId();
        ButtonCallback callback = new ButtonCallback(callbackName, messageSenderId, params.toParameters());
        return new MessageKeyboardButton(callback, "➡️ Next ➡️");
    }

    private MessageKeyboardButton addPreviousButton(ButtonCallback oldCallback, TrackMessage trackMessage) {
        TopTracksCallbackParams params = new TopTracksCallbackParams(oldCallback.getParameters());
        params.setOffset(params.getOffset() - params.getLimit());
        if (params.getOffset() < 0) {
            return null;
        }
        params.setTrackMessage(trackMessage);
        String callbackName = oldCallback.getCallbackName();
        String messageSenderId = oldCallback.getMessageSenderId();
        ButtonCallback callback = new ButtonCallback(callbackName, messageSenderId, params.toParameters());
        return new MessageKeyboardButton(callback, "⬅️️ Previous ⬅️");
    }

    private String getMessageText(Paging<Track> trackPage, TopTracksCallbackParams params) {
        int firstPlace = trackPage.getOffset() + 1;
        int lastPlace = trackPage.getOffset() + trackPage.getLimit();
        String period = params.getTimeRange().getDescription();
        return String.format("Your top %d...%d tracks for the %s", firstPlace, lastPlace, period);
    }
}
