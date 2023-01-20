package com.example.telegrambot.bot.service.toptracks;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
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

    public SendMessage prepareMenu(Message message) {
        Long userId = message.getFrom().getId();
        String chatId = message.getChatId().toString();
        SendMessage topTracksMenu = new SendMessage();
        topTracksMenu.setText("""
                Select time range:
                \uD83E\uDDA5 Long - all time \uD83E\uDDA5
                \uD83C\uDFCE Medium - 6 months \uD83C\uDFCE
                ⏰️ Short - 4 weeks ⏰️""");
        topTracksMenu.setChatId(chatId);
        InlineKeyboardMarkup markup = prepareInlineKeyboardMarkup(userId);
        topTracksMenu.setReplyMarkup(markup);
        return topTracksMenu;
    }

    private InlineKeyboardMarkup prepareInlineKeyboardMarkup(Long userId) {
        List<MessageKeyboardButton> buttons = prepareMessageKeyboardButtons(userId);
        List<MessageKeyboardRow> rows = prepareMessageKeyboardRows(buttons);
        MessageKeyboard keyboard = new MessageKeyboard(rows);
        return keyboard.toInlineKeyboardMarkup();
    }

    private List<MessageKeyboardRow> prepareMessageKeyboardRows(List<MessageKeyboardButton> buttons) {
        return buttons.stream()
                .map(MessageKeyboardRow::new)
                .toList();
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

        MessageKeyboardButton longRangeButton =
                new MessageKeyboardButton(longRangeButtonCallback, "\uD83E\uDDA5 Long \uD83E\uDDA5");
        MessageKeyboardButton mediumRangeButton =
                new MessageKeyboardButton(mediumRangeButtonCallback, "\uD83C\uDFCE Medium \uD83C\uDFCE");
        MessageKeyboardButton shortRangeButton =
                new MessageKeyboardButton(shortRangeButtonCallback, "⏰ Short ⏰");

        return List.of(longRangeButton, mediumRangeButton, shortRangeButton);
    }
}
