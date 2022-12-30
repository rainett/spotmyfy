package com.example.telegrambot.telegram.annotations;

@Executable
public @interface Callback {
    String callbackName();
    boolean fromSender() default false;
}
