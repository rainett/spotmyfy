package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams.TrackMessage;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.bot.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.enums.TimeRange;
import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.WebhookBot;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardButton;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.Track;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Callback(callbackName = "top_tracks", fromSender = true)
@Slf4j
public class TopTracksCallback {

    private final UserRepository userRepository;
    private final WebhookBot bot;
    private final SpotifyApiFactory spotifyApiFactory;


    @Runnable
    public void run(Update update) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String accessToken = userRepository.getByUserId(userId).orElseThrow().getAccessToken();

        ButtonCallback buttonCallback = new ButtonCallback(update);
        TopTracksCallbackParams params = new TopTracksCallbackParams(buttonCallback.getParameters());
        Paging<Track> trackPaging;
        try {
            trackPaging = getTrackPaging(accessToken, params);
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            onTrackPagingError(update, e);
            return;
        }
        try {
            if (params.getTrackMessage() != null) {
                Integer size = params.getTrackMessage().trackMessagesSize();
                int messageId = params.getTrackMessage().trackMessageId() + size;
                for (int i = 0; i < size; i++) {
                    bot.execute(new DeleteMessage(chatId, --messageId));
                }
            }
            SendMediaGroup sendMediaGroup = prepareSendTopTracks(update, trackPaging);
            List<Message> messages = bot.execute(sendMediaGroup);
            TrackMessage trackMessage = new TrackMessage(messages.get(0).getMessageId(), messages.size());
            EditMessageText editMessageText = prepareEditMessageText(update, trackPaging, trackMessage);
            bot.execute(editMessageText);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMediaGroup prepareSendTopTracks(Update update, Paging<Track> trackPaging) {
        Long userId = update.getCallbackQuery().getFrom().getId();
        SendMediaGroup sendMediaGroup = new SendMediaGroup();
        sendMediaGroup.setChatId(userId);

        List<InputMedia> inputMedia = new ArrayList<>();
        StringBuilder captionBuilder = new StringBuilder();
        int number = trackPaging.getOffset() + 1;
        String formatString = "\n\n№%d \uD83C\uDFB5%s — %s\uD83C\uDFB5\n<a href = \"%s\">Play in Spotify</a>";
        for (Track track : trackPaging.getItems()) {
            SimplifiedTrack t = new SimplifiedTrack(track);
            inputMedia.add(new InputMediaPhoto(t.getImageUrl()));
            captionBuilder.append(String.format(formatString, number++, t.getAuthor(), t.getName(), t.getUrl()));
        }
        InputMedia firstMedia = inputMedia.get(0);
        firstMedia.setParseMode("HTML");
        firstMedia.setCaption(captionBuilder.toString());
        sendMediaGroup.setMedias(inputMedia);
        return sendMediaGroup;
    }

    private void onTrackPagingError(Update update, Exception e) {
        log.error("Error during getting user's currently playing track", e);
        String text = "Oops! I can't get your currently playing track, maybe you should use /spotify command?";
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        try {
            bot.execute(new SendMessage(chatId, text));
        } catch (TelegramApiException ex) {
            ex.printStackTrace();
        }
    }

    private Paging<Track> getTrackPaging(String accessToken, TopTracksCallbackParams params)
            throws IOException, SpotifyWebApiException, ParseException {
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromAccessToken(accessToken);
        return spotifyApi.getUsersTopTracks()
                .time_range(params.getTimeRange().getCode())
                .limit(params.getLimit())
                .offset(params.getOffset())
                .build()
                .execute();
    }

    private EditMessageText prepareEditMessageText(Update update,
                                                   Paging<Track> trackPaging,
                                                   TrackMessage trackMessage) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        ButtonCallback buttonCallback = new ButtonCallback(update);
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        String textFormatted = getTextFormatted(buttonCallback, trackPaging);
        editMessageText.setText(textFormatted);
        InlineKeyboardMarkup markup = prepareReplyMarkup(trackPaging, buttonCallback, trackMessage);
        editMessageText.setReplyMarkup(markup);
        return editMessageText;
    }

    private String getTextFormatted(ButtonCallback buttonCallback, Paging<Track> trackPaging) {
        String topNumber = trackPaging.getOffset() + 1 + "..." + (trackPaging.getOffset() + trackPaging.getLimit());
        TimeRange timeRange = TimeRange.getByCode(buttonCallback.getParameters()[0]);
        return String.format("Your top %s tracks for the %s", topNumber, timeRange.getDescription());
    }

    private InlineKeyboardMarkup prepareReplyMarkup(Paging<Track> trackPaging,
                                                    ButtonCallback buttonCallback,
                                                    TrackMessage trackMessage) {
        List<MessageKeyboardButton> buttons = prepareButtons(trackPaging, buttonCallback, trackMessage);
        MessageKeyboardRow messageKeyboardRow = new MessageKeyboardRow(buttons);
        MessageKeyboard messageKeyboard = new MessageKeyboard(messageKeyboardRow);
        return messageKeyboard.toInlineKeyboardMarkup();
    }

    private List<MessageKeyboardButton> prepareButtons(Paging<Track> trackPaging,
                                                       ButtonCallback buttonCallback,
                                                       TrackMessage trackMessage) {
        List<MessageKeyboardButton> buttons = new ArrayList<>();
        if (trackPaging.getPrevious() != null) {
            buttons.add(preparePreviousButton(buttonCallback, trackMessage));
        }
        if (trackPaging.getNext() != null) {
            buttons.add(prepareNextButton(buttonCallback, trackMessage));
        }

        return buttons;
    }

    private MessageKeyboardButton prepareNextButton(ButtonCallback buttonCallback,
                                                    TrackMessage trackMessage) {
        TopTracksCallbackParams params = new TopTracksCallbackParams(buttonCallback.getParameters());
        params.setOffset(params.getOffset() + params.getLimit());
        params.setTrackMessage(trackMessage);
        String callbackName = buttonCallback.getCallbackName();
        String messageSenderIdStr = buttonCallback.getMessageSenderId();
        ButtonCallback callback = new ButtonCallback(callbackName, messageSenderIdStr, params.toParameters());
        return new MessageKeyboardButton(callback, "➡️ Next ➡️");
    }

    private MessageKeyboardButton preparePreviousButton(ButtonCallback buttonCallback,
                                                        TrackMessage trackMessage) {
        TopTracksCallbackParams params = new TopTracksCallbackParams(buttonCallback.getParameters());
        params.setOffset(params.getOffset() - params.getLimit());
        if (params.getOffset() < 0) {
            return null;
        }
        params.setTrackMessage(trackMessage);
        String callbackName = buttonCallback.getCallbackName();
        String messageSenderIdStr = buttonCallback.getMessageSenderId();
        ButtonCallback callback = new ButtonCallback(callbackName, messageSenderIdStr, params.toParameters());
        return new MessageKeyboardButton(callback, "⬅️️ Previous ⬅️️");
    }

}
