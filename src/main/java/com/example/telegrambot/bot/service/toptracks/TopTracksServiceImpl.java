package com.example.telegrambot.bot.service.toptracks;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.enums.TimeRange;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardButton;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;

@RequiredArgsConstructor
@Component
public class TopTracksServiceImpl implements TopTracksService {

    private static final Integer TRACKS_LIMIT = 10;
    private static final Integer TRACKS_OFFSET = 0;

    private final MessageService messageService;

    public SendMessage prepareMenu(Message message) {
        Long userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        SendMessage topTracksMenu = new SendMessage();
        String messageText = messageService.getMessage("command.top_tracks.greeting", userId);
        topTracksMenu.setText(messageText);
        topTracksMenu.setChatId(chatId);
        InlineKeyboardMarkup markup = prepareInlineKeyboardMarkup(userId);
        topTracksMenu.setReplyMarkup(markup);
        return topTracksMenu;
    }

    private InlineKeyboardMarkup prepareInlineKeyboardMarkup(Long userId) {
        List<MessageKeyboardButton> buttons = prepareMessageKeyboardButtons(userId);
        MessageKeyboardRow row = new MessageKeyboardRow(buttons);
        MessageKeyboard keyboard = new MessageKeyboard(row);
        return keyboard.toInlineKeyboardMarkup();
    }

    private List<MessageKeyboardButton> prepareMessageKeyboardButtons(Long userId) {
        TopTracksCallbackParams longParams =
                new TopTracksCallbackParams(TimeRange.LONG, TRACKS_LIMIT, TRACKS_OFFSET, null);
        TopTracksCallbackParams mediumParams =
                new TopTracksCallbackParams(TimeRange.MEDIUM, TRACKS_LIMIT, TRACKS_OFFSET, null);
        TopTracksCallbackParams shortParams =
                new TopTracksCallbackParams(TimeRange.SHORT, TRACKS_LIMIT, TRACKS_OFFSET, null);


        String callbackName = "top_tracks";
        String messageSenderId = userId.toString();
        ButtonCallback longRangeButtonCallback =
                new ButtonCallback(callbackName, messageSenderId, longParams.toParameters());
        ButtonCallback mediumRangeButtonCallback =
                new ButtonCallback(callbackName, messageSenderId, mediumParams.toParameters());
        ButtonCallback shortRangeButtonCallback =
                new ButtonCallback(callbackName, messageSenderId, shortParams.toParameters());

        String longText =
                messageService.getMessage("callback.top_tracks.button.range.long.text", userId);
        String mediumText =
                messageService.getMessage("callback.top_tracks.button.range.medium.text", userId);
        String shortText =
                messageService.getMessage("callback.top_tracks.button.range.short.text", userId);

        MessageKeyboardButton longRangeButton =
                new MessageKeyboardButton(longRangeButtonCallback, longText);
        MessageKeyboardButton mediumRangeButton =
                new MessageKeyboardButton(mediumRangeButtonCallback, mediumText);
        MessageKeyboardButton shortRangeButton =
                new MessageKeyboardButton(shortRangeButtonCallback, shortText);

        return List.of(longRangeButton, mediumRangeButton, shortRangeButton);
    }
}
