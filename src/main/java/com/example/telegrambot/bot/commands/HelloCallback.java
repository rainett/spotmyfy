package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.controller.executables.Executable;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Callback(callbackName = "hello")
public class HelloCallback implements Executable {


    @Override
    public List<PartialBotApiMethod<?>> run(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String messageText = "good job, nice click";

        SendMessage sendMessage = new SendMessage(chatId, messageText);
        return List.of(sendMessage);
    }
}
