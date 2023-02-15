package com.example.telegrambot.bot.service.authorization.uri;

import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.config.SpotifyConfig;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AuthorizationScope;

import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class AuthorizationURIServiceImpl implements AuthorizationURIService {

    private final SpotifyApiFactory spotifyApiFactory;
    private final SpotifyConfig spotifyConfig;
    private final MessageService messageService;

    @Override
    public SendMessage generateAuthorizationURI(Message message) {
        Long userId = message.getFrom().getId();
        Long chatId = message.getChatId();
        Integer messageId = message.getMessageId();
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromRedirectUri(spotifyConfig);
        AuthorizationScope[] scopes = Arrays.stream(spotifyConfig.getScopes())
                .map(AuthorizationScope::keyOf)
                .toArray(AuthorizationScope[]::new);
        String uri = spotifyApi.authorizationCodeUri()
                .scope(scopes)
                .build()
                .execute().toString();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("HTML");
        String text = messageService.getMessage("command.spotify.greeting", userId);
        sendMessage.setText(String.format("<a href=\"%s\">%s</a>", uri, text));
        sendMessage.setReplyToMessageId(messageId);
        return sendMessage;
    }
}
