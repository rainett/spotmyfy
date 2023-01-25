package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.language.LanguageService;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command("/language")
public class LanguageCommand {

    private final LanguageService languageService;
    private final BotExecutor bot;

    @Runnable
    public void run(Update update) {
        SendMessage sendMessage = languageService.getChangeLanguageMessage(update.getMessage());
        bot.execute(sendMessage);
    }

}
