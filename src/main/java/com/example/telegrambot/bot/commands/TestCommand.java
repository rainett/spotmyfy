package com.example.telegrambot.bot.commands;

import com.example.telegrambot.telegram.annotations.Command;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Command(name = "/deputat", description = "this is my command description")
public class TestCommand implements Executable {

    public List<PartialBotApiMethod<?>> run(Update update) {
        return List.of(prepareMenu(update));
    }

    private SendMessage prepareMenu(Update update) {
        return new SendMessage(update.getMessage().getChatId().toString(), "yep, i received your command");
    }

}
