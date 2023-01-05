package com.example.telegrambot.telegram.controller.executables.container.key.update;

import com.example.telegrambot.telegram.annotations.Command;
import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
public class CommandUpdate {

    private String commandName;

    public CommandUpdate(Command command) {
        commandName = command.name();
    }

    public CommandUpdate(Update update) {
        commandName = "/" + update.getMessage().getText().split("\\W")[1];
    }

    public static boolean updateMatches(Update update) {
        return update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/");
    }
}
