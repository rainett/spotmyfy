package com.example.telegrambot.bot.commands;

import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.elements.Track;
import com.example.telegrambot.spotify.model.UserCode;
import com.example.telegrambot.spotify.repository.UserCodeRepository;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.executables.container.BotMethods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Command(name = "/currently_playing")
public class CurrentlyPlayingCommand {

    private final UserCodeRepository userCodeRepository;

    @TokenRefresh
    @Runnable
    public BotMethods run(Update update) {
        BotMethods botMethods = new BotMethods();
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        Optional<UserCode> userCodeOptional = userCodeRepository.getByUserId(userId);

        if (userCodeOptional.isEmpty()) {
            String text = "Oops! I can't get your currently playing track, maybe you should use /spotify command?";
            botMethods.addMethod(new SendMessage(chatId, text));
            return botMethods;
        }

        SpotifyApi spotifyApi = getSpotifyApi(userCodeOptional);
        CurrentlyPlaying currentlyPlaying;
        try {
            currentlyPlaying = spotifyApi.getUsersCurrentlyPlayingTrack().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error during getting user's currently playing track", e);
            String text = "Oops! I can't get your currently playing track, maybe you should use /spotify command?";
            botMethods.addMethod(new SendMessage(chatId, text));
            return botMethods;
        }
        PartialBotApiMethod<?> method = getResponse(chatId, currentlyPlaying);
        return BotMethods.of(method);
    }

    private SpotifyApi getSpotifyApi(Optional<UserCode> userCodeOptional) {
        UserCode userCode = userCodeOptional.orElseThrow();
        String accessToken = userCode.getAccessToken();
        return SpotifyApiFactory.getSpotifyApiFromAccessToken(accessToken);
    }

    private PartialBotApiMethod<?> getResponse(String chatId, CurrentlyPlaying currentlyPlaying) {
        String text;
        if (currentlyPlaying != null) {
            Track track = new Track(currentlyPlaying);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(track.getImageUrl()));
            text = String.format("\uD83C\uDFB5%s — %s\uD83C\uDFB5\n\nPlay in Spotify - %s",
                    track.getAuthor(), track.getName(), track.getUrl());
            sendPhoto.setCaption(text);
            return sendPhoto;
        } else {
            SendMessage sendMessage = new SendMessage();
            text = "Where are your headphones? Your ass is not listening to any song!";
            sendMessage.setText(text);
            sendMessage.setChatId(chatId);
            return sendMessage;
        }
    }

}
