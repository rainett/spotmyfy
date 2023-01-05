package com.example.telegrambot.spotify.elements;

import lombok.Data;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.model_objects.miscellaneous.CurrentlyPlaying;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
public class Track {

    private String name;
    private String author;
    private String imageUrl;
    private String url;

    public Track(CurrentlyPlaying currentlyPlaying) {
        String currentlyPlayingString = currentlyPlaying.toString();
        this.name = parseName(currentlyPlayingString);
        this.author = parseAuthor(currentlyPlayingString);
        this.imageUrl = parseImage(currentlyPlayingString);
        this.url = parseUrl(currentlyPlayingString);
    }

    private String parseUrl(String currentlyPlayingString) {
        Pattern pattern = Pattern.compile("externalUrls=\\{spotify=(.+?)}");
        Matcher matcher = pattern.matcher(currentlyPlayingString);
        if (matcher.find()) {
            return (currentlyPlayingString.substring(matcher.start(1), matcher.end(1)));
        }
        return "https://i.ytimg.com/vi/DePeq2RY6xQ/maxresdefault.jpg";
    }

    private String parseImage(String currentlyPlayingString) {
        Pattern pattern = Pattern.compile("https?://i\\.scdn\\.co/image/\\w+");
        Matcher matcher = pattern.matcher(currentlyPlayingString);
        if (matcher.find()) {
            return (currentlyPlayingString.substring(matcher.start(0), matcher.end(0)));
        }
        return "https://i.ytimg.com/vi/DePeq2RY6xQ/maxresdefault.jpg";
    }

    private String parseAuthor(String currentlyPlayingString) {
        List<String> artistsList = new ArrayList<>();
        Pattern pattern = Pattern.compile("ArtistSimplified\\(name=(.+?),");
        Matcher matcher = pattern.matcher(currentlyPlayingString);
        while (matcher.find()) {
            artistsList.add(currentlyPlayingString.substring(matcher.start(1), matcher.end(1)));
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < artistsList.size() / 2; i++) {
            if ( i == artistsList.size() / 2 - 1) {
                sb.append(artistsList.get(i));
            } else {
                sb.append(String.format("%s, ", artistsList.get(i)));
            }
        }
        return sb.toString();
    }

    private String parseName(String currentlyPlayingString) {
        Pattern pattern = Pattern.compile("name=.+?,");
        Matcher matcher = pattern.matcher(currentlyPlayingString);
        if (matcher.find()) {
            return currentlyPlayingString.substring(matcher.start(0) + 5, matcher.end(0) - 1);
        }
        return "None";
    }
}
