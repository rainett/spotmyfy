package com.example.telegrambot.bot.callbacks;

import com.example.telegrambot.bot.service.language.callback.LanguageCallbackService;
import com.rainett.javagram.annotations.Callback;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
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
