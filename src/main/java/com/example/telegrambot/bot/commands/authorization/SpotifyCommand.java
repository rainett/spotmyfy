package com.example.telegrambot.bot.commands.authorization;

import com.example.telegrambot.bot.service.authorization.uri.AuthorizationURIService;
import lombok.RequiredArgsConstructor;
import org.rainett.telegram.annotations.Command;
import org.rainett.telegram.annotations.Run;
import org.rainett.telegram.controller.executor.BotExecutor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Command(value = "/spotify")
public class SpotifyCommand {

    private final AuthorizationURIService authorizationURIService;
    private final BotExecutor bot;

    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        String chatId = message.getChatId().toString();
        Long userId = message.getFrom().getId();
        SendMessage sendMessage = authorizationURIService
                .generateAuthorizationURI(chatId, userId, message.getMessageId());
        bot.execute(sendMessage);
    }

}
