package com.example.telegrambot.bot.service.language;

import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardButton;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
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
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String text = messageService.getMessage("command.language.text", userId);
        sendMessage.setText(text);
        InlineKeyboardMarkup markup = getInlineKeyboardMarkup(userId);
        sendMessage.setReplyMarkup(markup);
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
                    ButtonCallback callback = new ButtonCallback(callbackName, userId.toString(), parameters);
                    MessageKeyboardButton button = new MessageKeyboardButton(callback, l.flag);
                    buttons.add(button);
                });
        return buttons;
    }

}
