package com.example.telegrambot.telegram.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Executable
@Retention(RetentionPolicy.RUNTIME)
public @interface Callback {
    String callbackName();
    boolean fromSender() default false;
}
