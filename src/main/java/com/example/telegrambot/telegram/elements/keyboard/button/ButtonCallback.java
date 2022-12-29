package com.example.telegrambot.telegram.elements.keyboard.button;

import com.example.telegrambot.telegram.annotations.Callback;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;

@Getter
@Setter
public class ButtonCallback {

    private String name;
    private String fromId;
    private String messageFromId;
    private String[] params;

    public ButtonCallback(CallbackQuery callbackQuery) {
        String callbackData = callbackQuery.getData();
        fromId = callbackQuery.getFrom().getId().toString();
        parseCallbackData(callbackData);
    }

    public ButtonCallback(Message message, String name, String[] params) {
        this.name = name;
        this.messageFromId = message.getFrom().getId().toString();
        this.params = params;
    }

    private void parseCallbackData(String callbackData) {
        String[] callbackDataSplit = callbackData.split("\\?");
        int k = 0;
        name = callbackDataSplit[k++];
        messageFromId = callbackDataSplit[k++];
        params = Arrays.copyOfRange(callbackDataSplit, k, callbackDataSplit.length);
    }

    public String toCallbackData() {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        appendMessageFromId(builder);
        appendParams(builder);
        return builder.toString();
    }

    private void appendMessageFromId(StringBuilder builder) {
        if (messageFromId != null && !messageFromId.isEmpty()) {
            builder.append("?").append(messageFromId);
        }
    }

    private void appendParams(StringBuilder builder) {
        if (params == null) {
            return;
        }
        for (String param : params) {
            builder.append("?").append(param);
        }
    }

    public boolean matches(Callback callback) {
        String callbackName = callback.callbackName();
        boolean fromSender = callback.fromSender();
        return callbackName.equals(name) && (!fromSender || messageFromId.equals(fromId));
    }
}
