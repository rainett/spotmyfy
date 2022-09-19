package com.example.telegrambot.telegram;

import com.example.telegrambot.telegram.exceptions.ExecutableNotFoundException;
import com.example.telegrambot.telegram.exceptions.NotAnExecutableException;
import com.example.telegrambot.telegram.executable.ExecutablesContainer;
import com.example.telegrambot.telegram.service.UpdateProcessor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
public abstract class Bot extends TelegramLongPollingBot {

    private final String username;
    private final String token;
    private final ExecutablesContainer executables;
    private final UpdateProcessor updateProcessor;

    protected Bot(ExecutablesContainer executables, String username,
                  String token, UpdateProcessor updateProcessor) {
        this.executables = executables;
        this.username = username;
        this.token = token;
        this.updateProcessor = updateProcessor;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received an update: {}", update);
        try {
            List<PartialBotApiMethod<?>> actions = updateProcessor.process(update, executables);
            for (PartialBotApiMethod<?> action : actions) {
                executeGeneric(action);
            }
        } catch (ExecutableNotFoundException e) {
            log.error("Command not found: {}", e.getMessage());
        } catch (NotAnExecutableException e) {
            log.error("Received update was not an executable");
        }
    }

    private void executeGeneric(PartialBotApiMethod<?> method) {
        for (Method m : this.getClass().getMethods()) {
            if (m.getName().equals("execute") && m.getParameters()[0].getType().equals(method.getClass())) {
                try {
                    m.invoke(this, method);
                    return;
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
        try {
            this.execute((BotApiMethod<? extends Serializable>) method);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
