package com.example.telegrambot.telegram.controller.executables.container;

import com.example.telegrambot.telegram.annotations.Executable;
import com.example.telegrambot.telegram.config.BotConfig;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

@Component
public class ExecutablesContainerImpl implements ExecutablesContainer {

    @Getter(AccessLevel.PUBLIC)
    private final Map<String, Object> executables;
    private final BotConfig botConfig;

    @Autowired
    public ExecutablesContainerImpl(ApplicationContext appContext, BotConfig botConfig) {
        this.executables = appContext.getBeansWithAnnotation(Executable.class);
        this.botConfig = botConfig;
    }

    @Override
    public Object getExecutable(Update update) {
        return null;
    }

}