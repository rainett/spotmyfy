package com.example.telegrambot.bot.service.propertymessage;

import com.example.telegrambot.bot.repository.UserLanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@RequiredArgsConstructor
@Component
public class MessageServiceImpl implements MessageService {

    private final UserLanguageRepository userLanguageRepository;
    private final MessageSource messageSource;

    @Override
    public String getMessage(String code, Long userId) {
        String localeCode = userLanguageRepository.findByUserId(userId).getLocaleCode();
        Locale locale = Locale.forLanguageTag(localeCode);
        return messageSource.getMessage(code, null, locale);
    }

}
