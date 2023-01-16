package com.example.telegrambot.bot.service.authorization;

import com.example.telegrambot.bot.User;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.bot.service.authorization.AuthorizationService;
import com.example.telegrambot.bot.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.example.telegrambot.spotify.config.SpotifyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyConfig spotifyConfig;

    @Override
    public String authorize(Update update) {
        String[] messageSplit = update.getMessage().getText().split(" ");
        if (messageSplit.length == 2) {
            Long codeId = Long.parseLong(messageSplit[1]);
            Long userId = update.getMessage().getFrom().getId();
            return processAuthorizationCode(codeId, userId);
        }
        return "Hi! Send me /spotify command to go to authentication";
    }

    private String processAuthorizationCode(Long codeId, Long userId) {
        Optional<User> userOptional = userRepository.findById(codeId);
        if (userOptional.isEmpty()) {
            return "Oops! I can't find your Spotify account. Try using ";
        }
        User user = userOptional.get();
        userRepository.delete(user);
        return authorizeUser(userId, user.getCode());
    }

    private String authorizeUser(Long userId, String code) {
        SpotifyApi api = spotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        AuthorizationCodeCredentials credentials = getAuthorizationCodeCredentials(code, api);
        User user = userRepository.getFromCredentials(credentials, userId);
        userRepository.save(user);
        return "Logged in successfully!";
    }

    private AuthorizationCodeCredentials getAuthorizationCodeCredentials(String code, SpotifyApi api) {
        try {
            return api.authorizationCode(code).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            String errorMessage = "Error during authorization";
            log.error(errorMessage, e);
            throw new AuthorizationFailedException(errorMessage);
        }
    }

}
