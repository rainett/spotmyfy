package com.example.telegrambot.spotify.exceptions;

public class AuthorizationFailedException extends RuntimeException {
    public AuthorizationFailedException(String errorMessage) {
        super(errorMessage);
    }
}
