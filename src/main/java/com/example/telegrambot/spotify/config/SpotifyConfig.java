package com.example.telegrambot.spotify.config;

import com.example.telegrambot.bot.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.config.TokenRefreshingBeanPostProcessor;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.telegram.controller.WebhookBot;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class SpotifyConfig {

    @Value("${spotify.client_id}")
    private String clientId;

    @Value("${spotify.client_secret}")
    private String clientSecret;

    @Value("${bot.path}" + "${spotify.redirect_uri}")
    private String redirectUri;

    @Value("${spotify.scopes}")
    private String[] scopes;

    @Bean
    public TokenRefreshingBeanPostProcessor tokenRefreshingBeanPostProcessor(
            UserRepository userRepository,
            WebhookBot bot,
            SpotifyApiFactory spotifyApiFactory) {
        return new TokenRefreshingBeanPostProcessor(userRepository, bot, this, spotifyApiFactory);
    }

}
