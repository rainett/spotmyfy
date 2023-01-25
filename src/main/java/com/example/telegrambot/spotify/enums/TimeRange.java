package com.example.telegrambot.spotify.enums;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

@Getter
public enum TimeRange {

    LONG("long_term"),
    MEDIUM("medium_term"),
    SHORT("short_term");

    private final String code;

    TimeRange(String code) {
        this.code = code;
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
