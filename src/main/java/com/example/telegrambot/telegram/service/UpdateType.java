package com.example.telegrambot.telegram.service;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Slf4j
public enum UpdateType {

    CALLBACK_QUERY, CHANNEL_POST, CHAT_JOIN_REQUEST, CHAT_MEMBER, CHOSEN_INLINE_QUERY,
    EDITED_CHANNEL_POST, EDITED_MESSAGE, INLINE_QUERY, MESSAGE, MY_CHAT_MEMBER,
    POLL, POLL_ANSWER, PRE_CHECKOUT_QUERY, SHIPPING, QUERY;

    public static UpdateType getByUpdate(Update update) {
        Method[] methods = Update.class.getMethods();
        for (Method method : methods) {
            if (method.getReturnType().equals(boolean.class) && method.getName().startsWith("has")) {
                try {
                    if ((Boolean) method.invoke(update)) {
                        return valueOf(camelToSnake(method.getName()).substring(4).toUpperCase());
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Caught an exception during getting update type, {}", update);
                }
            }
        }
        throw new IllegalArgumentException();
    }

    private static String camelToSnake(String str)
    {
        StringBuilder result = new StringBuilder();
        char c = str.charAt(0);
        result.append(Character.toLowerCase(c));
        for (int i = 1; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                result.append('_');
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

}
