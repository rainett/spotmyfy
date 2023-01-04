package com.example.telegrambot.telegram.elements.keyboard;

import lombok.*;

import java.util.Arrays;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class ButtonCallback {

    private final String callbackName;
    private final String messageSenderId;
    private String[] parameters;

    public String toCallbackData() {
        StringBuilder builder = new StringBuilder();
        builder.append(callbackName);
        builder.append("?").append(messageSenderId);
        if (parameters != null)
            Arrays.stream(parameters).forEach(p -> builder.append("?").append(p));
        return builder.toString();
    }
}
