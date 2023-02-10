package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.language.LanguageService;
import com.rainett.javagram.annotations.Command;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
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
