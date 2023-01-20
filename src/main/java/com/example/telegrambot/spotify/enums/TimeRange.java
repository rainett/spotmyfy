package com.example.telegrambot.spotify.enums;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public enum TimeRange {

    LONG("long_term", "all time"),
    MEDIUM("medium_term", "last 6 months"),
    SHORT("short_term", "last 4 weeks");

    private final String code;
    private final String description;

    TimeRange(String code, String time) {
        this.code = code;
        this.description = time;
    }

    public static TimeRange getByCode(String code) {
        for (TimeRange timeRange : TimeRange.values()) {
            if (timeRange.getCode().contains(code)) {
                return timeRange;
            }
        }
        return MEDIUM;
    }
}