package com.example.telegrambot.bot.service.language.callback;

import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface LanguageCallbackService {
    AnswerCallbackQuery getAnswer(CallbackQuery callbackQuery);
}
