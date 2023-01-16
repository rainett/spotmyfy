package com.example.telegrambot.bot.service.currentlyplaying;

import com.example.telegrambot.bot.User;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.bot.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.exceptions.CurrentlyPlayingNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import com.example.telegrambot.spotify.exceptions.UserNotListeningException;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CurrentlyPlayingServiceImpl implements CurrentlyPlayingService {

    private final UserRepository userRepository;
    private final SpotifyApiFactory spotifyApiFactory;

    public SimplifiedTrack getCurrentlyPlayingTrack(Long userId)
            throws UserNotFoundException, CurrentlyPlayingNotFoundException, UserNotListeningException {
        String accessToken = getAccessToken(userId);
        CurrentlyPlaying currentlyPlaying = getCurrentlyPlaying(accessToken);
        return new SimplifiedTrack(currentlyPlaying);
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

    private String getAccessToken(Long userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.getByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userOptional.get().getAccessToken();
    }
}
