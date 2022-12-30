package com.example.telegrambot.telegram.annotations;

@Executable
public @interface Command {
    String name();
    String description() default "ㅤ";
}
