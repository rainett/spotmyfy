package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command(name = "/pic", description = "sends picture")
public class SendRandomPicCommand {

    @Runnable
    public BotMethods run(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        String messageResponse = "okay, let me send you something...";
        SendMessage sendMessage = new SendMessage(chatId, messageResponse);
        SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile("https://cdn.motor1.com/images/mgl/9Yngp/s1/toyota-all-electric-3te25-towing-tractor.webp"));
        BotMethods botMethods = new BotMethods();
        botMethods.addMethod(sendMessage);
        botMethods.addMethod(sendPhoto);
        return botMethods;
    }

}
