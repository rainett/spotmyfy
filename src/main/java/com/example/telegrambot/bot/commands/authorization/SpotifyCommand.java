package com.example.telegrambot.bot.commands.authorization;

import com.example.telegrambot.bot.service.authorization.uri.AuthorizationURIService;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.net.URI;

@RequiredArgsConstructor
@Command(name = "/spotify")
public class SpotifyCommand {

    private final AuthorizationURIService authorizationURIService;
    private final BotExecutor bot;

    @Runnable
    public void run(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        URI uri = authorizationURIService.generateAuthorizationURI();
        sendMessage.setParseMode("MarkdownV2");
        sendMessage.setText("[Click me to proceed to Spotify authorization](" + uri.toString() + ")");
        bot.execute(sendMessage);
    }

}
