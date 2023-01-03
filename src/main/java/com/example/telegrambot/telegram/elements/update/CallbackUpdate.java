package com.example.telegrambot.telegram.elements.update;

import com.example.telegrambot.telegram.annotations.Callback;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
public class CallbackUpdate {

    private String callbackName;
    private boolean fromSender;

    public CallbackUpdate(Callback callback) {
        callbackName = callback.callbackName();
        fromSender = callback.fromSender();
    }

    public CallbackUpdate(Update update) {
        CallbackQuery callbackQuery = update.getCallbackQuery();
        String[] callbackDataSplit = callbackQuery.getData().split("\\?");
        callbackName = callbackDataSplit[0];
        fromSender = callbackQuery.getFrom().getId().toString().equals(callbackDataSplit[1]);
    }

    public static boolean updateMatches(Update update) {
        return update.hasCallbackQuery();
    }

    public String toCallbackData() {
        throw new UnsupportedOperationException();
    }
}
