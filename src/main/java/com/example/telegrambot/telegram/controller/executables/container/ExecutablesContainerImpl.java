package com.example.telegrambot.telegram.controller.executables.container;

import com.example.telegrambot.telegram.annotations.Executable;
import com.example.telegrambot.telegram.controller.executables.container.key.UpdateKey;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExecutablesContainerImpl implements ExecutablesContainer {

    @Getter(AccessLevel.PUBLIC)
    private final Map<UpdateKey, Object> executables;
    @Autowired
    public ExecutablesContainerImpl(ApplicationContext appContext) {
        this.executables = appContext.getBeansWithAnnotation(Executable.class)
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                entry -> UpdateKey.getInstance(entry.getValue()),
                                Map.Entry::getValue)
                        );
    }

    @Override
    public Optional<Object> getExecutable(Update update) {
        return Optional.ofNullable(executables.get(UpdateKey.getInstance(update)));
    }

}