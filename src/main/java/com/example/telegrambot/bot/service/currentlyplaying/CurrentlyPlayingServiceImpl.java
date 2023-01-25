package com.example.telegrambot.bot.service.currentlyplaying;

import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CurrentlyPlayingServiceImpl implements CurrentlyPlayingService {

    private final UserRepository userRepository;
    private final SpotifyApiFactory spotifyApiFactory;

    @Override
    public SendPhoto prepareSendPhoto(Message message) throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException {
        SendPhoto sendPhoto = new SendPhoto();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        sendPhoto.setChatId(chatId);
        SimplifiedTrack currentlyPlaying = getCurrentlyPlayingTrack(userId);
        sendPhoto.setPhoto(new InputFile(currentlyPlaying.getImageUrl()));
        sendPhoto.setParseMode("HTML");
        sendPhoto.setCaption(currentlyPlaying.toTextMessage());
        return sendPhoto;
    }

    public SimplifiedTrack getCurrentlyPlayingTrack(Long userId)
            throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException {
        String accessToken = getAccessToken(userId);
        CurrentlyPlaying currentlyPlaying = getCurrentlyPlaying(accessToken);
        return new SimplifiedTrack(currentlyPlaying);
    }

    private String getAccessToken(Long userId) throws UserNotFoundException {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = [" + userId + "] was not found"))
                .getAccessToken();
    }

    private CurrentlyPlaying getCurrentlyPlaying(String accessToken)
            throws UserNotListeningException, CurrentlyPlayingNotFoundException {
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromAccessToken(accessToken);
        CurrentlyPlaying currentlyPlaying;
        try {
            currentlyPlaying = spotifyApi.getUsersCurrentlyPlayingTrack().build().execute();
            if (currentlyPlaying == null) {
                throw new UserNotListeningException();
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new CurrentlyPlayingNotFoundException(e);
        }
        return currentlyPlaying;
    }

}
