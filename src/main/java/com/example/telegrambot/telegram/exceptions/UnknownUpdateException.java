package com.example.telegrambot.telegram.exceptions;

public class UnknownUpdateException extends RuntimeException {
    public UnknownUpdateException(String s) {
        super(s);
    }
}
