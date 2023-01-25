package com.example.telegrambot.bot.service.language;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum Language {
    UA("\uD83C\uDDFA\uD83C\uDDE6", "uk-UA"), US("\uD83C\uDDEC\uD83C\uDDE7", "en-US");

    public final String flag;
    public final String code;

    public static Language fromCode(String code) {
        return Arrays.stream(Language.values()).filter(l -> l.code.equals(code)).findAny().orElseThrow();
    }
}
