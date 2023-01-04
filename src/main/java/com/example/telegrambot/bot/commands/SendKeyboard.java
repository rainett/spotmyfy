package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import com.example.telegrambot.telegram.elements.keyboard.ButtonCallback;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboard;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardButton;
import com.example.telegrambot.telegram.elements.keyboard.MessageKeyboardRow;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command(name = "/keyboard")
public class SendKeyboard {

    @Runnable
    public BotMethods run(Update update) {
        SendMessage sendMessage = prepareSendMessage(update);
        return BotMethods.of(sendMessage);
    }

    private SendMessage prepareSendMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String messageText = "text of this message\n\n in`it?)";
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(messageText);

        MessageKeyboard keyboard = prepareMessageKeyboard(update);

        sendMessage.setReplyMarkup(keyboard.toInlineKeyboardMarkup());
        return sendMessage;
    }

    private MessageKeyboard prepareMessageKeyboard(Update update) {
        String messageFromId = update.getMessage().getFrom().getId().toString();
        String callbackName = "aboba";
        ButtonCallback callback = new ButtonCallback(callbackName, messageFromId);
        MessageKeyboardButton button = new MessageKeyboardButton(callback, "click me");
        MessageKeyboardRow row = new MessageKeyboardRow(button);
        return new MessageKeyboard(row);
    }

}
