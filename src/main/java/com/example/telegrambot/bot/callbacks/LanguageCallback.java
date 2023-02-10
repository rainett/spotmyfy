package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.bot.service.language.callback.LanguageCallbackService;
import lombok.RequiredArgsConstructor;
import org.rainett.telegram.annotations.Callback;
import org.rainett.telegram.annotations.Run;
import org.rainett.telegram.controller.executor.BotExecutor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Callback(value = "language")
public class LanguageCallback {

    private final LanguageCallbackService languageCallbackService;
    private final BotExecutor bot;

    @Run
    public void run(Update update) {
        AnswerCallbackQuery answer = languageCallbackService.getAnswer(update.getCallbackQuery());
        bot.execute(answer);
    }

}
