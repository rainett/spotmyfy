package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.TimeUnit;

@Command(name = "/sleep", description = "makes me sleep for 5 seconds")
public class SleepCommand {

    @Runnable
    public BotMethods run(Update update) {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return BotMethods.of(new SendMessage(update.getMessage().getChatId().toString(), "booo!!!"));
    }

}
