package com.example.telegrambot.bot.service.authorization;

import com.example.telegrambot.bot.model.AuthorizationCode;
import com.example.telegrambot.bot.model.User;
import com.example.telegrambot.bot.repository.AuthorizationCodeRepository;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.config.SpotifyConfig;
import com.example.telegrambot.spotify.exceptions.AuthorizationCodeNotFound;
import com.example.telegrambot.spotify.exceptions.AuthorizationFailedException;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Component
public class AuthorizationServiceImpl implements AuthorizationService {

    private final UserRepository userRepository;
    private final AuthorizationCodeRepository authorizationCodeRepository;
    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyConfig spotifyConfig;
    private final MessageService messageService;

    @Override
    public SendMessage authorize(Update update)
            throws AuthorizationFailedException, AuthorizationCodeNotFound {
        Message message = update.getMessage();
        Long chatId = message.getChatId();
        String text = getMessageText(message);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyToMessageId(message.getMessageId());
        return sendMessage;
    }

    public String getMessageText(Message message) throws AuthorizationFailedException, AuthorizationCodeNotFound {
        String[] messageSplit = message.getText().split(" ");
        Long userId = message.getFrom().getId();
        if (messageSplit.length != 2) {
            return messageService.getMessage("command.start.greeting", userId);
        }
        Long codeId = Long.parseLong(messageSplit[1]);
        return processAuthorizationCode(codeId, userId);
    }

    private String processAuthorizationCode(Long codeId, Long userId)
            throws AuthorizationFailedException, AuthorizationCodeNotFound {
        String errorMessage = "Authorization code with id = [" + userId + "] was not found";
        AuthorizationCode authorizationCode = authorizationCodeRepository.findById(codeId)
                .orElseThrow(() -> new AuthorizationCodeNotFound(errorMessage));
        authorizationCodeRepository.delete(authorizationCode);
        return authorizeUser(userId, authorizationCode.getCode());
    }

    private String authorizeUser(Long userId, String code) throws AuthorizationFailedException {
        SpotifyApi api = spotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        AuthorizationCodeCredentials credentials = getAuthorizationCodeCredentials(code, api);
        User user = new User(userId, credentials.getAccessToken(),
                credentials.getRefreshToken(), LocalDateTime.now());
        userRepository.save(user);
        return messageService.getMessage("command.start.logged_in", userId);
    }

    private AuthorizationCodeCredentials getAuthorizationCodeCredentials(String code, SpotifyApi api)
            throws AuthorizationFailedException {
        try {
            return api.authorizationCode(code).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new AuthorizationFailedException();
        }
    }

}
