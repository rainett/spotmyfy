package com.example.telegrambot.spotify.utils;

import com.example.telegrambot.spotify.config.SpotifyConfig;
import se.michaelthelin.spotify.SpotifyApi;

public interface SpotifyApiFactory {
    SpotifyApi getSpotifyApiFromRedirectUri(SpotifyConfig spotifyConfig);
    SpotifyApi getSpotifyApiFromAccessToken(String accessToken);
    SpotifyApi getSpotifyApiFromRefreshToken(SpotifyConfig spotifyConfig, String refreshToken);

}
