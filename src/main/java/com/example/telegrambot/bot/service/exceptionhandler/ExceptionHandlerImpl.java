package com.example.telegrambot.bot.service.exceptionhandler;

import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.exceptions.*;
import com.rainett.javagram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

@Slf4j
@RequiredArgsConstructor
@Component
public class ExceptionHandlerImpl implements ExceptionHandler {

    private final BotExecutor bot;
    private final MessageService messageService;

    @Override
    public void userNotFound(Message message, UserNotFoundException e) {
        log.error("User was not found", e);
        sendReport(message, "exception.user_not_found");
    }

    @Override
    public void userNotFoundCallback(CallbackQuery callbackQuery, UserNotFoundException e) {
        log.error("User was not found", e);
        sendCallbackReport(callbackQuery, "exception.user_not_found");
    }

    @Override
    public void currentlyPlayingNotFound(Message message, CurrentlyPlayingNotFoundException e) {
        log.error("Failed to get user's currently playing track", e);
        sendReport(message, "exception.currently_playing_not_found");
    }

    @Override
    public void userNotListening(Message message) {
        sendReport(message, "exception.user_not_listening");
    }

    @Override
    public void topTracksCallback(CallbackQuery callbackQuery, TopTracksException e) {
        log.error("Failed to get user's top tracks", e);
        sendCallbackReport(callbackQuery, "exception.top_tracks");
    }

    @Override
    public void authorizationFailed(Message message, AuthorizationFailedException e) {
        log.error("Failed to perform authorization", e);
        sendReport(message, "exception.authorization_failed");
    }

    @Override
    public void authorizationCodeNotFound(Message message, AuthorizationCodeNotFound e) {
        log.error("Failed to perform authorization", e);
        sendReport(message, "exception.authorization_code_not_found");
    }

    @Override
    public void audioFeaturesNotFound(Message message, AudioFeaturesNotFoundException e) {
        log.error("Failed to find audio features", e);
        sendReport(message, "exception.audio_features_not_found");
    }

    private void sendReport(Message message, String exceptionCode) {
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        Integer messageId = message.getMessageId();
        String text = messageService.getMessage(exceptionCode, userId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyToMessageId(messageId);

        bot.execute(sendMessage);
    }

    private void sendCallbackReport(CallbackQuery callbackQuery, String s) {
        String callbackQueryId = callbackQuery.getId();
        Long userId = callbackQuery.getFrom().getId();
        String text = messageService.getMessage(s, userId);

        AnswerCallbackQuery query = new AnswerCallbackQuery();
        query.setCallbackQueryId(callbackQueryId);
        query.setText(text);
        query.setShowAlert(true);

        bot.execute(query);
    }

}
