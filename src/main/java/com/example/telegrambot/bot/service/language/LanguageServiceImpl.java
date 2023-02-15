package com.example.telegrambot.bot.service.language;

import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.rainett.javagram.keyboard.ButtonCallback;
import com.rainett.javagram.keyboard.MessageKeyboard;
import com.rainett.javagram.keyboard.MessageKeyboardButton;
import com.rainett.javagram.keyboard.MessageKeyboardRow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Component
public class LanguageServiceImpl implements LanguageService {

    private final MessageService messageService;

    @Override
    public SendMessage getChangeLanguageMessage(Message message) {
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        String text = messageService.getMessage("command.language.text", userId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup(userId);
        sendMessage.setReplyMarkup(markup);
        sendMessage.setReplyToMessageId(message.getMessageId());

        return sendMessage;
    }

    private InlineKeyboardMarkup getInlineKeyboardMarkup(Long userId) {
        List<MessageKeyboardButton> buttons = getButtons(userId);
        MessageKeyboardRow row = new MessageKeyboardRow(buttons);
        MessageKeyboard keyboard = new MessageKeyboard(row);
        return keyboard.toInlineKeyboardMarkup();
    }

    private List<MessageKeyboardButton> getButtons(Long userId) {
        List<MessageKeyboardButton> buttons = new ArrayList<>();
        Arrays.stream(Language.values())
                .forEach(l -> {
                    String callbackName = "language";
                    String[] parameters = new String[]{l.code};
                    String senderId = userId.toString();
                    ButtonCallback callback = new ButtonCallback(callbackName, senderId, parameters);
                    MessageKeyboardButton button = new MessageKeyboardButton(callback, l.flag);
                    buttons.add(button);
                });
        return buttons;
    }

}
