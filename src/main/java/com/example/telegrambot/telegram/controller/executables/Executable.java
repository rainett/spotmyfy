package com.example.telegrambot.telegram.controller.executables;

import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.config.BotConfig;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Executable {

    String COMMAND_REGEX = "^%s(%s)?($|( \\w+){%d})";

    List<PartialBotApiMethod<?>> run(Update update);

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
            return updateMatchesCommand(update, botUsername, command);
        }
        if (isAnnotation(annotation, Callback.class)) {
            // todo: process callback
        }

        return false;
    }

    private boolean updateMatchesCommand(Update update, String botUsername, Command command) {
        String messageText = update.getMessage().getText();
        String commandName = command.name();
        int parametersNumber = command.params().length;
        String formattedRegex = String.format(COMMAND_REGEX, commandName, botUsername, parametersNumber);
        return messageText.matches(formattedRegex);
    }

    private boolean isAnnotation(Annotation a, Class<?> annotationType) {
        return a.annotationType().equals(annotationType);
    }

}
