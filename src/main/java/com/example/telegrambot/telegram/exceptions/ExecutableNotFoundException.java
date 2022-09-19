package com.example.telegrambot.telegram.exceptions;

public class ExecutableNotFoundException extends Exception {
    public ExecutableNotFoundException(String trigger) {
        super(trigger);
    }
}
