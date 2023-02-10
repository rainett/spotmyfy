package com.example.telegrambot.bot.commands.authorization;

import com.example.telegrambot.bot.service.authorization.AuthorizationService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.exceptions.AuthorizationCodeNotFound;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rainett.telegram.annotations.Command;
import org.rainett.telegram.annotations.Run;
import org.rainett.telegram.controller.executor.BotExecutor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command(value = "/start")
@RequiredArgsConstructor
@Slf4j
public class StartCommand {

    private final AuthorizationService authorizationService;
    private final ExceptionHandler handler;
    private final BotExecutor bot;

    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        try {
            bot.execute(authorizationService.authorize(update));
        } catch (AuthorizationFailedException e) {
            handler.authorizationFailed(message, e);
        } catch (AuthorizationCodeNotFound e) {
            handler.authorizationCodeNotFound(message, e);
        }
    }

}
