package com.example.telegrambot.telegram.controller.executables.container.key;

import com.example.telegrambot.telegram.annotations.Callback;
import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.elements.update.CallbackUpdate;
import com.example.telegrambot.telegram.elements.update.CommandUpdate;
import com.example.telegrambot.telegram.exceptions.UnknownUpdateException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.annotation.Annotation;

@Data
@Builder(access = AccessLevel.PACKAGE)
@Slf4j
public class UpdateKey {

    private CommandUpdate commandUpdate;
    private CallbackUpdate callbackUpdate;

    public static UpdateKey getInstance(Object value) throws UnknownUpdateException {
        if (hasAnnotation(value, Command.class)) {
            return KeyBuilder.ofCommand(value.getClass().getAnnotation(Command.class));
        }
        if (hasAnnotation(value, Callback.class)) {
            return KeyBuilder.ofCallback(value.getClass().getAnnotation(Callback.class));
        }
        if (isUpdate(value)) {
            return KeyBuilder.ofUpdate((Update) value);
        }
        throw new UnknownUpdateException("Unknown update was passed as a parameter");
    }

    private static boolean isUpdate(Object value) {
        return value instanceof Update;
    }

    private static boolean hasAnnotation(Object value, Class<? extends Annotation> annotationClass) {
        return value.getClass().isAnnotationPresent(annotationClass);
    }

}
