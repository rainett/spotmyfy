package com.example.telegrambot.spotify.exceptions;

public class CurrentlyPlayingNotFoundException extends Exception {
    public CurrentlyPlayingNotFoundException(Exception e) {
        super(e);
    }

    public CurrentlyPlayingNotFoundException() {
    }
}
