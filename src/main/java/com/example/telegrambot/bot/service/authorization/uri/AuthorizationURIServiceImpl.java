package com.example.telegrambot.bot.service.authorization.uri;

import com.example.telegrambot.bot.service.authorization.uri.AuthorizationURIService;
import com.example.telegrambot.bot.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.config.SpotifyConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
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
    public URI generateAuthorizationURI() {
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        AuthorizationScope[] scopes = Arrays.stream(spotifyConfig.getScopes())
                .map(AuthorizationScope::keyOf)
                .toArray(AuthorizationScope[]::new);
        return spotifyApi.authorizationCodeUri()
                .scope(scopes)
                .build()
                .execute();
    }
}
