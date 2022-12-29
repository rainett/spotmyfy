package com.example.telegrambot.telegram.controller.executables;

import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.elements.keyboard.button.ButtonCallback;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Executable {

    List<PartialBotApiMethod<?>> run(Update update);

    String COMMAND_REGEX = "^%s(%s)?($|( \\w+)*)";

    default boolean matches(Update update, String botUsername) {
        Class<? extends Executable> type = this.getClass();
        Annotation annotation = type.getAnnotations()[0];
        if (annotation == null) {
            return false;
        }
        if (isAnnotation(annotation, Command.class)) {
            Command command = type.getAnnotation(Command.class);
            if (!update.hasMessage() || !update.getMessage().hasText()) {
                return false;
            }
            return updateMatchesCommand(update, command, botUsername);
        }
        if (isAnnotation(annotation, Callback.class)) {
            Callback callback = type.getAnnotation(Callback.class);
            if (!update.hasCallbackQuery() || update.getCallbackQuery().getData() == null) {
                return false;
            }
            return updateMatchesCallback(update.getCallbackQuery(), callback);
        }

        return false;
    }

    private boolean updateMatchesCallback(CallbackQuery callbackQuery, Callback callback) {
        ButtonCallback buttonCallback = new ButtonCallback(callbackQuery);
        return buttonCallback.matches(callback);
    }

    private boolean updateMatchesCommand(Update update, Command command, String botUsername) {
        String messageText = update.getMessage().getText();
        String commandName = command.name();
        String formattedRegex = String.format(COMMAND_REGEX, commandName, botUsername);
        return messageText.matches(formattedRegex);
    }

    private boolean isAnnotation(Annotation a, Class<?> annotationType) {
        return a.annotationType().equals(annotationType);
    }

}
