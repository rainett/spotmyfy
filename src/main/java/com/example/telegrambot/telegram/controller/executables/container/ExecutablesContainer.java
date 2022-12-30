package com.example.telegrambot.telegram.controller.executables.container;

import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

public interface ExecutablesContainer {

    Object getExecutable(Update update);

    Set<Object> getExecutables();

}
