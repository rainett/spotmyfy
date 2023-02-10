package com.example.telegrambot.bot.service.language.callback;

import com.example.telegrambot.bot.model.UserLanguage;
import com.example.telegrambot.bot.repository.UserLanguageRepository;
import com.example.telegrambot.bot.service.language.Language;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.rainett.javagram.keyboard.ButtonCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@RequiredArgsConstructor
@Component
public class LanguageCallbackServiceImpl implements LanguageCallbackService {

    private final MessageService messageService;
    private final UserLanguageRepository userLanguageRepository;

    @Override
    public AnswerCallbackQuery getAnswer(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getFrom().getId();
        String callbackQueryId = callbackQuery.getId();
        updateUserLanguage(callbackQuery, userId);
        return getAnswerCallbackQuery(userId, callbackQueryId);
    }

    private void updateUserLanguage(CallbackQuery callbackQuery, Long userId) {
        ButtonCallback callback = new ButtonCallback(callbackQuery);
        Language language = Language.fromCode(callback.getParameters()[0]);
        UserLanguage userLanguage = userLanguageRepository.findByUserId(userId);
        userLanguage.setLocaleCode(language.code);
        userLanguageRepository.save(userLanguage);
    }

    private AnswerCallbackQuery getAnswerCallbackQuery(Long userId, String callbackQueryId) {
        AnswerCallbackQuery answer = new AnswerCallbackQuery();
        answer.setCallbackQueryId(callbackQueryId);
        String text = messageService.getMessage("callback.language.success", userId);
        answer.setText(text);
        answer.setShowAlert(true);
        return answer;
    }
}
