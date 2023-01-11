package com.example.telegrambot.bot.commands;

import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.config.SpotifyConfig;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.enums.AuthorizationScope;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;

import java.net.URI;

@Command(name = "/spotify")
public class SpotifyCommand {

    private final AuthorizationCodeUriRequest AUTHORIZATION_CODE_URI_REQUEST;

    @Autowired
    public SpotifyCommand(SpotifyConfig spotifyConfig) {
        SpotifyApi spotifyApi = SpotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        this.AUTHORIZATION_CODE_URI_REQUEST =
                spotifyApi.authorizationCodeUri()
                        .scope(AuthorizationScope.USER_READ_CURRENTLY_PLAYING)
                        .build();
    }

    @Runnable
    public BotMethods run(Update update) {
        String chatId = update.getMessage().getChatId().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        URI uri = AUTHORIZATION_CODE_URI_REQUEST.execute();
        sendMessage.setParseMode("MarkdownV2");
        sendMessage.setText("[click me](" + uri.toString() + ")");
        return BotMethods.of(sendMessage);
    }

}
