package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.controller.executables.Executable;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
import com.example.telegrambot.telegram.elements.keyboard.button.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.button.MessageKeyboardButton;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Command(name = "/keyboard", description = "send keyboard")
public class KeyboardCommand implements Executable {
    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        SendMessage keyboardMessage = getSendMessage(update);
        log.info("Composed sendMessage: {}", keyboardMessage);
        return List.of(keyboardMessage);
    }

    private SendMessage getSendMessage(Update update) {
        SendMessage keyboardMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        String messageText = "Now yuo cee...";
        keyboardMessage.setChatId(chatId);
        keyboardMessage.setText(messageText);
        MessageKeyboard keyboard = getMessageKeyboard(update.getMessage());
        keyboardMessage.setReplyMarkup(keyboard.toInlineKeyboardMarkup());
        return keyboardMessage;
    }

    private MessageKeyboard getMessageKeyboard(Message message) {
        ButtonCallback callback = new ButtonCallback(message, "hello", null);
        MessageKeyboardButton button = new MessageKeyboardButton(callback, "baton.");
        MessageKeyboardRow row = new MessageKeyboardRow(button);
        return new MessageKeyboard(row);
    }
}
