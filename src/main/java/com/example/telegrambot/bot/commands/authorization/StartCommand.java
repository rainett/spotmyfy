package com.example.telegrambot.bot.commands.authorization;

import com.example.telegrambot.bot.service.authorization.StartService;
import com.example.telegrambot.bot.service.exceptionhandler.ExceptionHandler;
import com.example.telegrambot.spotify.exceptions.AuthorizationCodeNotFound;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.rainett.javagram.annotations.Command;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.async.BotExecutorAsync;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Command(value = "/start")
@RequiredArgsConstructor
@Slf4j
public class StartCommand {

    private final StartService startService;
    private final ExceptionHandler handler;
    private final BotExecutorAsync bot;

    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        try {
            bot.execute(startService.start(message));
        } catch (AuthorizationFailedException e) {
            handler.authorizationFailed(message, e);
        } catch (AuthorizationCodeNotFound e) {
            handler.authorizationCodeNotFound(message, e);
        }
    }

}
