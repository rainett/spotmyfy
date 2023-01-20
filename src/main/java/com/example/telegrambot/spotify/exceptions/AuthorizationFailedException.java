package com.example.telegrambot.spotify.exceptions;

public class AuthorizationFailedException extends Exception {
    public AuthorizationFailedException(String errorMessage) {
        super(errorMessage);
    }
}
