package com.example.telegrambot.telegram.controller.executables.container;

import com.example.telegrambot.telegram.config.BotConfig;
import com.example.telegrambot.telegram.controller.executables.Executable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExecutablesContainerImpl implements ExecutablesContainer {

    @Getter(AccessLevel.PUBLIC)
    private final Set<Executable> executables;
    private final BotConfig botConfig;

    @Override
    public Executable getExecutable(Update update) {
        for (Executable e : executables) {
            if (e.matches(update, botConfig.getUsername())) {
                return e;
            }
        }
        return u -> List.of();
    }

}