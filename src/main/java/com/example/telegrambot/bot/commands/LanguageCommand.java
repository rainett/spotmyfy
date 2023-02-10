package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.language.LanguageService;
import lombok.RequiredArgsConstructor;
import org.rainett.telegram.annotations.Command;
import org.rainett.telegram.annotations.Run;
import org.rainett.telegram.controller.executor.BotExecutor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command("/language")
public class LanguageCommand {

    private final LanguageService languageService;
    private final BotExecutor bot;

    @Run
    public void run(Update update) {
        SendMessage sendMessage = languageService.getChangeLanguageMessage(update.getMessage());
        bot.execute(sendMessage);
    }

}
