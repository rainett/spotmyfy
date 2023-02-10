package com.example.telegrambot.bot.service.toptracks.callback;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams.TrackMessage;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.TopTracksException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.rainett.javagram.keyboard.ButtonCallback;
import com.rainett.javagram.keyboard.MessageKeyboard;
import com.rainett.javagram.keyboard.MessageKeyboardButton;
import com.rainett.javagram.keyboard.MessageKeyboardRow;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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
    private final MessageService messageService;


    @Override
    public Paging<Track> getTrackPage(Long userId, TopTracksCallbackParams params)
            throws UserNotFoundException, TopTracksException {
        String accessToken = userRepository.findById(userId)
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
    public List<EditMessageMedia> getEditMedias(Paging<Track> trackPage,
                                                TrackMessage trackMessage,
                                                CallbackQuery callbackQuery) {
        List<EditMessageMedia> editMediaList = new ArrayList<>();
        Long chatId = callbackQuery.getMessage().getChatId();
        Long userId = callbackQuery.getFrom().getId();
        int messageId = trackMessage.trackMessageId();
        StringBuilder stringBuilder = new StringBuilder();
        Track[] tracks = trackPage.getItems();
        for (int i = 0; i < tracks.length; i++) {
            SimplifiedTrack track = new SimplifiedTrack(tracks[i]);
            EditMessageMedia e = new EditMessageMedia();
            e.setChatId(chatId);
            e.setMessageId(i + messageId);
            e.setMedia(new InputMediaPhoto(track.getImageUrl()));
            editMediaList.add(e);
            stringBuilder.append(getToAppend(trackPage.getOffset() + i + 1, userId, track));
        }
        InputMedia firstMedia = editMediaList.get(0).getMedia();
        firstMedia.setParseMode("HTML");
        firstMedia.setCaption(stringBuilder.toString());
        return editMediaList;
    }

    private String getToAppend(int place, Long userId, SimplifiedTrack track) {
        String messageFormat = messageService.getMessage("element.track.text", userId);
        String format = "\n\n№%d " + track.formatString(messageFormat);
        return String.format(format, place);
    }

    @Override
    public SendMediaGroup getMediaGroup(Paging<Track> trackPage, CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        Long userId = callbackQuery.getFrom().getId();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(chatId);
        List<InputMedia> medias = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        Track[] tracks = trackPage.getItems();
        for (int i = 0; i < tracks.length; i++) {
            SimplifiedTrack track = new SimplifiedTrack(tracks[i]);
            InputMediaPhoto photo = new InputMediaPhoto();
            photo.setMedia(track.getImageUrl());
            medias.add(photo);
            stringBuilder.append(getToAppend(trackPage.getOffset() + i + 1, userId, track));
        }
        InputMedia firstMedia = medias.get(0);
        firstMedia.setParseMode("HTML");
        firstMedia.setCaption(stringBuilder.toString());
        sendMediaGroup.setMedias(medias);
        sendMediaGroup.setReplyToMessageId(callbackQuery.getMessage().getMessageId());
        return sendMediaGroup;
    }

    @Override
    public EditMessageText getEditText(Update update, Paging<Track> trackPage, TrackMessage trackMessage) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long userId = update.getCallbackQuery().getFrom().getId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        ButtonCallback callback = new ButtonCallback(update.getCallbackQuery());
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        String text = getMessageText(trackPage, new TopTracksCallbackParams(callback.getParameters()), userId);
        editMessageText.setText(text);

        List<MessageKeyboardButton> buttons = getButtons(trackPage, callback, trackMessage, userId);
        MessageKeyboardRow row = new MessageKeyboardRow(buttons);
        MessageKeyboard messageKeyboard = new MessageKeyboard(row);
        InlineKeyboardMarkup replyMarkup = messageKeyboard.toInlineKeyboardMarkup();
        editMessageText.setReplyMarkup(replyMarkup);
        return editMessageText;
    }

    private List<MessageKeyboardButton> getButtons(Paging<Track> trackPage,
                                                   ButtonCallback oldCallback,
                                                   TrackMessage trackMessage,
                                                   Long userId) {
        List<MessageKeyboardButton> buttons = new ArrayList<>();
        if (trackPage.getPrevious() != null) {
            buttons.add(addPreviousButton(oldCallback, trackMessage, userId));
        }
        if (trackPage.getNext() != null) {
            buttons.add(addNextButton(oldCallback, trackMessage, userId));
        }

        return buttons;
    }

    private MessageKeyboardButton addNextButton(ButtonCallback oldCallback,
                                                TrackMessage trackMessage,
                                                Long userId) {
        TopTracksCallbackParams params = new TopTracksCallbackParams(oldCallback.getParameters());
        params.setOffset(params.getOffset() + params.getLimit());
        params.setTrackMessage(trackMessage);
        String callbackName = oldCallback.getCallbackName();
        String messageSenderId = oldCallback.getMessageSenderId();
        ButtonCallback callback = new ButtonCallback(callbackName, messageSenderId, params.toParameters());
        String buttonText =
                messageService.getMessage("callback.top_tracks.button.next.text", userId);
        return new MessageKeyboardButton(callback, buttonText);
    }

    private MessageKeyboardButton addPreviousButton(ButtonCallback oldCallback,
                                                    TrackMessage trackMessage,
                                                    Long userId) {
        TopTracksCallbackParams params = new TopTracksCallbackParams(oldCallback.getParameters());
        params.setOffset(params.getOffset() - params.getLimit());
        if (params.getOffset() < 0) {
            return null;
        }
        params.setTrackMessage(trackMessage);
        String callbackName = oldCallback.getCallbackName();
        String messageSenderId = oldCallback.getMessageSenderId();
        ButtonCallback callback = new ButtonCallback(callbackName, messageSenderId, params.toParameters());
        String buttonText =
                messageService.getMessage("callback.top_tracks.button.previous.text", userId);
        return new MessageKeyboardButton(callback, buttonText);
    }

    private String getMessageText(Paging<Track> trackPage, TopTracksCallbackParams params, Long userId) {
        int firstPlace = trackPage.getOffset() + 1;
        int lastPlace = trackPage.getOffset() + trackPage.getLimit();
        String timeRangeCode = "callback.top_tracks.time." + params.getTimeRange().getCode();
        String period = messageService.getMessage(timeRangeCode, userId);
        String format = messageService.getMessage("callback.top_tracks.message.text", userId);
        return String.format(format, firstPlace, lastPlace, period);
    }
}
