package com.example.telegrambot.telegram.controller;

import com.example.telegrambot.telegram.annotations.Command;
import com.example.telegrambot.telegram.config.BotConfig;
import com.example.telegrambot.telegram.controller.executables.Executable;
import com.example.telegrambot.telegram.controller.executables.container.ExecutablesContainer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Getter
@Setter
@Component
@Slf4j
public class WebhookBot extends SpringWebhookBot {

    private BotConfig botConfig;

    private final ExecutablesContainer executablesContainer;

    public WebhookBot(SetWebhook setWebhook, BotConfig botConfig, ExecutablesContainer executablesContainer) {
        super(setWebhook);
        this.botConfig = botConfig;
        this.executablesContainer = executablesContainer;
        setCommandsDescriptions(executablesContainer);
    }

    private void setCommandsDescriptions(ExecutablesContainer executablesContainer) {
        List<BotCommand> commands = executablesContainer.getExecutables().stream()
                .filter(exe -> exe.getClass().isAnnotationPresent(Command.class))
                .map(exe -> new BotCommand(
                        exe.getClass().getAnnotation(Command.class).name(),
                        exe.getClass().getAnnotation(Command.class).description()))
                .toList();
        this.tryExecute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        Executable executable = executablesContainer.getExecutable(update);
        log.info("Executable received {}", executable.getClass().getName());
        List<PartialBotApiMethod<?>> executableResult = executable.run(update);
        executableResult.forEach(this::executeGeneric);
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    private void executeGeneric(PartialBotApiMethod<?> method) {
        log.info("Executing bot method {}", method.getClass().getSimpleName());
        for (Method m : this.getClass().getMethods()) {
            if (m.getName().equals("execute") && m.getParameters()[0].getType().equals(method.getClass())) {
                tryExecuteReflect(method, m);
                return;
            }
        }
        tryExecute(method);
    }

    private void tryExecute(PartialBotApiMethod<?> method) {
        try {
            this.execute((BotApiMethod<? extends Serializable>) method);
        } catch (TelegramApiException e) {
            log.error("Error executing {}", method);
            log.error("Error caused ", e);
        }
    }

    private void tryExecuteReflect(PartialBotApiMethod<?> method, Method m) {
        try {
            m.invoke(this, method);
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error executing {}", method);
            log.error("Error caused ", e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }
}
