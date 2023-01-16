package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.callbacks.TopTracksCallbackParams;
import com.example.telegrambot.spotify.enums.TimeRange;
import com.example.telegrambot.bot.User;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardButton;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Command(name = "/top_tracks")
public class TopTracksCommand {

    private final UserRepository userRepository;
    private final BotExecutor bot;
    private static final Integer TRACKS_LIMIT = 10;
    private static final Integer TRACKS_OFFSET = 0;

    @Runnable
    public void run(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        Optional<User> userCodeOptional = userRepository.getByUserId(userId);
        if (userCodeOptional.isEmpty()) {
            String text = "Hm, i was unable to find you account. Try using /spotify command";
            bot.execute(new SendMessage(chatId, text));
            return;
        }

        SendMessage topTracksMenu = new SendMessage();
        topTracksMenu.setText("""
                Select time range:
                \uD83E\uDDA5 Long - all time \uD83E\uDDA5
                \uD83C\uDFCE Medium - 6 months \uD83C\uDFCE
                ⏰️ Short - 4 weeks ⏰️""");
        topTracksMenu.setChatId(chatId);
        InlineKeyboardMarkup markup = prepareInlineKeyboardMarkup(userId);
        topTracksMenu.setReplyMarkup(markup);
        bot.execute(topTracksMenu);
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

        MessageKeyboardButton longRangeButton =
                new MessageKeyboardButton(longRangeButtonCallback, "\uD83E\uDDA5 Long \uD83E\uDDA5");
        MessageKeyboardButton mediumRangeButton =
                new MessageKeyboardButton(mediumRangeButtonCallback, "\uD83C\uDFCE Medium \uD83C\uDFCE");
        MessageKeyboardButton shortRangeButton =
                new MessageKeyboardButton(shortRangeButtonCallback, "⏰ Short ⏰");

        return List.of(longRangeButton, mediumRangeButton, shortRangeButton);
    }

}
