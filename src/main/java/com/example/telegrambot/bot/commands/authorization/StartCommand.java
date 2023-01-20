package com.example.telegrambot.bot.commands.authorization;

import com.example.telegrambot.bot.service.authorization.AuthorizationService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command(name = "/start")
@RequiredArgsConstructor
@Slf4j
public class StartCommand {

    private final AuthorizationService authorizationService;
    private final ExceptionHandler handler;
    private final BotExecutor bot;

    @Runnable
    public void run(Update update) {
        SendMessage sendMessage = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        sendMessage.setChatId(chatId);
        String text;
        try {
            text = authorizationService.authorize(update);
        } catch (AuthorizationFailedException e) {
            handler.authorizationFailed(chatId.toString(), e);
            return;
        }
        sendMessage.setText(text);
        bot.execute(sendMessage);
    }

}
