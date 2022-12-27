package com.example.telegrambot.telegram.controller.executables.container;

import com.example.telegrambot.telegram.controller.executables.Executable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

public interface ExecutablesContainer {

    Executable getExecutable(Update update);

    Set<Executable> getExecutables();

}
