package com.example.telegrambot.bot.service.toptracks;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TopTracksService {
    SendMessage prepareMenu(Message message);
}
