package com.example.telegrambot.bot.commands;

import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.elements.SimplifiedTrack;
import com.example.telegrambot.spotify.model.UserCode;
import com.example.telegrambot.spotify.repository.UserCodeRepository;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.annotations.Runnable;
import com.example.telegrambot.telegram.controller.WebhookBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
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
    private final WebhookBot bot;

    @TokenRefresh
    @Runnable
    public void run(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        String chatId = update.getMessage().getChatId().toString();
        Optional<UserCode> userCodeOptional = userCodeRepository.getByUserId(userId);

        if (userCodeOptional.isEmpty()) {
            String text = "Oops! I can't get your currently playing track, maybe you should use /spotify command?";
            try {
                bot.execute(new SendMessage(chatId, text));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;
        }
        UserCode userCode = userCodeOptional.get();
        SpotifyApi spotifyApi = SpotifyApiFactory.getSpotifyApiFromAccessToken(userCode.getAccessToken());
        CurrentlyPlaying currentlyPlaying;
        try {
            currentlyPlaying = spotifyApi.getUsersCurrentlyPlayingTrack().build().execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            log.error("Error during getting user's currently playing track", e);
            String text = "Oops! I can't get your currently playing track, maybe you should use /spotify command?";
            try {
                bot.execute(new SendMessage(chatId, text));
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }
            return;
        }
        sendResponse(chatId, currentlyPlaying);
    }


    private void sendResponse(String chatId, CurrentlyPlaying currentlyPlaying) {
        String text;
        if (currentlyPlaying != null) {
            SimplifiedTrack track = new SimplifiedTrack(currentlyPlaying);
            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(new InputFile(track.getImageUrl()));
            sendPhoto.setParseMode("HTML");
            String formatString = "\n\n\uD83C\uDFB5%s — %s\uD83C\uDFB5\n<a href = \"%s\">Play in Spotify</a>";
            text = String.format(formatString, track.getAuthor(), track.getName(), track.getUrl());
            sendPhoto.setCaption(text);
            try {
                bot.execute(sendPhoto);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        } else {
            SendMessage sendMessage = new SendMessage();
            text = "Where are your headphones? Your ass is not listening to any song!";
            sendMessage.setText(text);
            sendMessage.setChatId(chatId);
            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

}
