package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.bot.service.language.callback.LanguageCallbackService;
import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Callback(value = "language")
public class LanguageCallback {

    private final LanguageCallbackService languageCallbackService;
    private final BotExecutor bot;

    @Runnable
    public void run(Update update) {
        AnswerCallbackQuery answer = languageCallbackService.getAnswer(update.getCallbackQuery());
        bot.execute(answer);
    }

}
