package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Callback(callbackName = "aboba", fromSender = true)
public class RetrieveCallback {

    @Runnable
    public BotMethods run(Update update) {
        String chatId = update.getCallbackQuery().getMessage().getChatId().toString();
        String text = "nice click bro";
        SendMessage sendMessage = new SendMessage(chatId, text);
        return BotMethods.of(sendMessage);
    }

}
