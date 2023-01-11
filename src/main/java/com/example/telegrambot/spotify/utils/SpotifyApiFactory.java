package com.example.telegrambot.spotify.utils;

import com.example.telegrambot.telegram.config.SpotifyConfig;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;

import java.net.URI;

public class SpotifyApiFactory {

    public static SpotifyApi getSpotifyApiFromRedirectUri(SpotifyConfig spotifyConfig) {
        URI redirectUri = SpotifyHttpManager.makeUri(spotifyConfig.getRedirectUri());
        return SpotifyApi.builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .setRedirectUri(redirectUri)
                .build();
    }

    public static SpotifyApi getSpotifyApiFromAccessToken(String accessToken) {
        return SpotifyApi.builder()
                .setAccessToken(accessToken)
                .build();
    }

    public static SpotifyApi getSpotifyApiFromRefreshToken(SpotifyConfig spotifyConfig, String refreshToken) {
        return SpotifyApi.builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .setRefreshToken(refreshToken)
                .build();
    }
}
