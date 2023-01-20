package com.example.telegrambot.spotify.utils;

import com.example.telegrambot.spotify.config.SpotifyConfig;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

@Component
public class SpotifyApiFactoryImpl implements SpotifyApiFactory {

    @Override
    public SpotifyApi getSpotifyApiFromRedirectUri(SpotifyConfig spotifyConfig) {
        URI redirectUri = SpotifyHttpManager.makeUri(spotifyConfig.getRedirectUri());
        return SpotifyApi.builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .setRedirectUri(redirectUri)
                .build();
    }

    @Override
    public SpotifyApi getSpotifyApiFromAccessToken(String accessToken) {
        return SpotifyApi.builder()
                .setAccessToken(accessToken)
                .build();
    }

    @Override
    public SpotifyApi getSpotifyApiFromRefreshToken(SpotifyConfig spotifyConfig, String refreshToken) {
        return SpotifyApi.builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .setRefreshToken(refreshToken)
                .build();
    }
}
