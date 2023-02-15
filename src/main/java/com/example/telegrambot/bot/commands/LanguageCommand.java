package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.language.LanguageService;
import com.rainett.javagram.annotations.Command;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.async.BotExecutorAsync;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command(value = "/language", description = "Language menu")
public class LanguageCommand {

    private final LanguageService languageService;
    private final BotExecutorAsync bot;

    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        SendMessage sendMessage = languageService.getChangeLanguageMessage(message);
        bot.execute(sendMessage);
    }

}
