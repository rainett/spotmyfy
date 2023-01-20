package com.example.telegrambot.spotify.config;

import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
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
            ApplicationContext applicationContext,
            SpotifyApiFactory spotifyApiFactory) {
        return new TokenRefreshingBeanPostProcessor(userRepository, applicationContext,
                this, spotifyApiFactory);
    }

}
