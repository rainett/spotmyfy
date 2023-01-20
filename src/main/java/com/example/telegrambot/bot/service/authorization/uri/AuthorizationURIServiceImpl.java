package com.example.telegrambot.bot.service.authorization.uri;

import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.config.SpotifyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AuthorizationScope;

import java.net.URI;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class AuthorizationURIServiceImpl implements AuthorizationURIService {

    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyConfig spotifyConfig;

    @Override
    public SendMessage generateAuthorizationURI(String chatId) {
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        AuthorizationScope[] scopes = Arrays.stream(spotifyConfig.getScopes())
                .map(AuthorizationScope::keyOf)
                .toArray(AuthorizationScope[]::new);
        URI uri = spotifyApi.authorizationCodeUri()
                .scope(scopes)
                .build()
                .execute();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("HTML");
        sendMessage.setText("[Click me to proceed to Spotify authorization](" + uri.toString() + ")");
        return sendMessage;
    }
}
