package com.example.telegrambot.bot.service.language;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface LanguageService {
    SendMessage getChangeLanguageMessage(Message message);
}
