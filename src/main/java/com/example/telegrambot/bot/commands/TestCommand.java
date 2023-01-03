package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command(name = "/deputat", description = "this is my command description")
public class TestCommand {

    @Runnable
    public BotMethods run(Update update) {
        BotMethods botMethods = new BotMethods();
        botMethods.addMethod(prepareMessage(update));
        return botMethods;
    }

    private SendMessage prepareMessage(Update update) {
        return new SendMessage(update.getMessage().getChatId().toString(), "yep, i received your command");
    }

}
