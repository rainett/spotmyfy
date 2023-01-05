package com.example.telegrambot.bot.commands;

import com.example.telegrambot.spotify.model.UserCode;
import com.example.telegrambot.spotify.repository.UserCodeRepository;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.config.SpotifyConfig;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Command(name = "/start")
@RequiredArgsConstructor
@Slf4j
public class StartCommand {

    private final UserCodeRepository userCodeRepository;
    private final SpotifyConfig spotifyConfig;

    @Runnable
    public BotMethods run(Update update) {
        Long chatId = update.getMessage().getChatId();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        String[] messageSplit = update.getMessage().getText().split(" ");

        if (messageSplit.length == 2) {
            processAuthorizationCode(sendMessage, Long.parseLong(messageSplit[1]), update);
        } else {
            sendMessage.setText("Hi! Send me /spotify command to go to authentication");
        }

        return BotMethods.of(sendMessage);
    }

    private void processAuthorizationCode(SendMessage sendMessage, Long codeId, Update update) {
        UserCode codeTemp = userCodeRepository.getReferenceById(codeId);
        String code = codeTemp.getCode();
        userCodeRepository.delete(codeTemp);
        URI redirectUri = SpotifyHttpManager.makeUri(spotifyConfig.getRedirectUri());
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(spotifyConfig.getClientId())
                .setClientSecret(spotifyConfig.getClientSecret())
                .setRedirectUri(redirectUri)
                .build();
        AuthorizationCodeCredentials authorizationCodeRequest = getAuthorizationCodeCredentials(code, spotifyApi);
        if (authorizationCodeRequest == null)
            return;
        String accessToken = authorizationCodeRequest.getAccessToken();
        String refreshToken = authorizationCodeRequest.getRefreshToken();
        Long userId = update.getMessage().getFrom().getId();
        UserCode userCode = getUserCode(accessToken, refreshToken, userId);
        userCodeRepository.save(userCode);
        sendMessage.setText("Logged in successfully!");
    }

    private UserCode getUserCode(String accessToken, String refreshToken, Long userId) {
        UserCode userCode = userCodeRepository.getByUserId(userId).orElse(new UserCode());
        userCode.setUserId(userId);
        userCode.setAccessToken(accessToken);
        userCode.setRefreshToken(refreshToken);
        return userCode;
    }

    private AuthorizationCodeCredentials getAuthorizationCodeCredentials(String code, SpotifyApi spotifyApi) {
        try {
            return spotifyApi
                    .authorizationCode(code)
                    .build()
                    .execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error executing authorization", e);
            return null;
        }
    }

}
