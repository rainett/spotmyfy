package com.example.telegrambot.bot.commands;

import com.example.telegrambot.spotify.model.UserCode;
import com.example.telegrambot.spotify.repository.UserCodeRepository;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
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
        String text;
        if (messageSplit.length == 2) {
            text = processAuthorizationCode(Long.parseLong(messageSplit[1]), update);
        } else {
            text = "Hi! Send me /spotify command to go to authentication";
        }
        sendMessage.setText(text);
        return BotMethods.of(sendMessage);
    }

    private String processAuthorizationCode(Long codeId, Update update) {
        String code = getCode(codeId);
        SpotifyApi spotifyApi = SpotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        AuthorizationCodeCredentials authorizationCodeCredentials = getAuthorizationCodeCredentials(code, spotifyApi);
        if (authorizationCodeCredentials == null)
            return "Oops, got an error during your authorization...";
        Long userId = update.getMessage().getFrom().getId();
        UserCode userCode = userCodeRepository.getFromRefreshToken(authorizationCodeCredentials, userId);
        userCodeRepository.save(userCode);
        return "Logged in successfully!";
    }

    private String getCode(Long codeId) {
        UserCode codeTemp = userCodeRepository.getReferenceById(codeId);
        String code = codeTemp.getCode();
        userCodeRepository.delete(codeTemp);
        return code;
    }

    private AuthorizationCodeCredentials getAuthorizationCodeCredentials(String code, SpotifyApi spotifyApi) {
        try {
            return spotifyApi
                    .authorizationCode(code)
                    .build()
                    .execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error getting authorization credentials", e);
            return null;
        }
    }

}
