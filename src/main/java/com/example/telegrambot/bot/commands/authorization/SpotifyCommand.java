package com.example.telegrambot.bot.commands.authorization;

import com.example.telegrambot.bot.service.authorization.uri.AuthorizationURIService;
import com.rainett.javagram.annotations.Command;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.async.BotExecutorAsync;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command(value = "/spotify", description = "Get spotify authorization link")
public class SpotifyCommand {

    private final AuthorizationURIService authorizationURIService;
    private final BotExecutorAsync bot;

    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        SendMessage sendMessage = authorizationURIService.generateAuthorizationURI(message);
        bot.execute(sendMessage);
    }

}
