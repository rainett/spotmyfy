package com.example.telegrambot.bot.service.toptracks;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.enums.TimeRange;
import com.rainett.javagram.keyboard.ButtonCallback;
import com.rainett.javagram.keyboard.MessageKeyboard;
import com.rainett.javagram.keyboard.MessageKeyboardButton;
import com.rainett.javagram.keyboard.MessageKeyboardRow;
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
    private static final String CALLBACK_NAME = "top_tracks";

    private final MessageService messageService;

    public SendMessage prepareMenu(Message message) {
        Long userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        String messageText = messageService.getMessage("command.top_tracks.greeting", userId);
        InlineKeyboardMarkup markup = prepareInlineKeyboardMarkup(userId);

        SendMessage topTracksMenu = new SendMessage();
        topTracksMenu.setText(messageText);
        topTracksMenu.setChatId(chatId);
        topTracksMenu.setReplyMarkup(markup);
        topTracksMenu.setReplyToMessageId(message.getMessageId());

        return topTracksMenu;
    }

    private InlineKeyboardMarkup prepareInlineKeyboardMarkup(Long userId) {
        List<MessageKeyboardButton> buttons = prepareMessageKeyboardButtons(userId);
        MessageKeyboardRow row = new MessageKeyboardRow(buttons);
        MessageKeyboard keyboard = new MessageKeyboard(row);
        return keyboard.toInlineKeyboardMarkup();
    }

    private List<MessageKeyboardButton> prepareMessageKeyboardButtons(Long userId) {
        MessageKeyboardButton longRangeButton =
                getButton(TimeRange.LONG, userId, "callback.top_tracks.button.range.long.text");
        MessageKeyboardButton mediumRangeButton =
                getButton(TimeRange.MEDIUM, userId, "callback.top_tracks.button.range.medium.text");
        MessageKeyboardButton shortRangeButton =
                getButton(TimeRange.SHORT, userId, "callback.top_tracks.button.range.short.text");

        return List.of(longRangeButton, mediumRangeButton, shortRangeButton);
    }

    private MessageKeyboardButton getButton(TimeRange timeRange, Long userId, String textCode) {
        TopTracksCallbackParams params =
                new TopTracksCallbackParams(timeRange, TRACKS_LIMIT, TRACKS_OFFSET, null);
        String messageSenderId = userId.toString();
        ButtonCallback buttonCallback =
                new ButtonCallback(CALLBACK_NAME, messageSenderId, params.toStringArray());
        String text = messageService.getMessage(textCode, userId);
        return new MessageKeyboardButton(buttonCallback, text);
    }
}
