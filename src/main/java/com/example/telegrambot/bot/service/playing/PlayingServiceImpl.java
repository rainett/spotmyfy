package com.example.telegrambot.bot.service.playing;

import com.example.telegrambot.bot.service.currentlyplaying.CurrentlyPlayingService;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

@RequiredArgsConstructor
@Component
public class PlayingServiceImpl implements PlayingService {

    private final CurrentlyPlayingService currentlyPlayingService;
    private final MessageService messageService;

    @Override
    public SendPhoto prepareSendPhoto(Message message)
            throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException {
        SendPhoto sendPhoto = new SendPhoto();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        sendPhoto.setChatId(chatId);
        SimplifiedTrack currentlyPlaying = currentlyPlayingService.getCurrentlyPlayingTrack(userId);
        sendPhoto.setPhoto(new InputFile(currentlyPlaying.getImageUrl()));
        sendPhoto.setParseMode("HTML");
        String caption = getCaption(userId, currentlyPlaying);
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyToMessageId(message.getMessageId());
        return sendPhoto;
    }

    private String getCaption(Long userId, SimplifiedTrack track) {
        String format = messageService.getMessage("element.track.text", userId);
        return track.formatString(format);
    }

}
