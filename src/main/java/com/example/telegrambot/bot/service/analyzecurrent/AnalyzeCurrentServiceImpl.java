package com.example.telegrambot.bot.service.analyzecurrent;

import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.bot.service.currentlyplaying.CurrentlyPlayingService;
import com.example.telegrambot.bot.service.propertymessage.MessageService;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.elements.SimplifiedTrackFeatures;
import com.example.telegrambot.spotify.exceptions.AudioFeaturesNotFoundException;
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
import se.michaelthelin.spotify.model_objects.specification.AudioFeatures;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class AnalyzeCurrentServiceImpl implements AnalyzeCurrentService {

    private final CurrentlyPlayingService currentlyPlayingService;
    private final UserRepository userRepository;
    private final SpotifyApiFactory spotifyApiFactory;
    private final MessageService messageService;

    @Override
    public SendPhoto getCurrentAnalysis(Message message)
            throws UserNotFoundException, CurrentlyPlayingNotFoundException,
            UserNotListeningException, AudioFeaturesNotFoundException {
        Long userId = message.getFrom().getId();
        SimplifiedTrack track = currentlyPlayingService.getCurrentlyPlayingTrack(userId);
        AudioFeatures audioFeatures = getAudioFeatures(track.getId(), userId);
        return getSendPhoto(message, track, audioFeatures);
    }

    private AudioFeatures getAudioFeatures(String trackId, Long userId)
            throws AudioFeaturesNotFoundException, UserNotFoundException {
        String accessToken = userRepository.getByUserId(userId).getAccessToken();
        SpotifyApi spotifyApi = spotifyApiFactory.getSpotifyApiFromAccessToken(accessToken);
        try {
            return spotifyApi.getAudioFeaturesForTrack(trackId).build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new AudioFeaturesNotFoundException(e);
        }
    }

    private SendPhoto getSendPhoto(Message message, SimplifiedTrack track, AudioFeatures audioFeatures) {
        Integer messageId = message.getMessageId();
        Long chatId = message.getChatId();
        Long userId = message.getFrom().getId();
        String caption = getCaption(track, audioFeatures, userId);

        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(track.getImageUrl()));
        sendPhoto.setCaption(caption);
        sendPhoto.setParseMode("HTML");
        sendPhoto.setReplyToMessageId(messageId);

        return sendPhoto;
    }

    private String getCaption(SimplifiedTrack track, AudioFeatures audioFeatures, Long userId) {
        SimplifiedTrackFeatures features = new SimplifiedTrackFeatures(audioFeatures);
        String formatAnalysis = messageService.getMessage("element.track_features.text", userId);
        String analysisString = features.formatString(formatAnalysis);

        String formatTrack = messageService.getMessage("element.track.text", userId);
        String trackString = track.formatString(formatTrack);

        return trackString + "\n\n" + analysisString;
    }
}
