package com.example.telegrambot.telegram.executable;

import com.example.telegrambot.telegram.service.UpdateType;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

public abstract class ExecutablesContainer {

    protected List<? extends Executable> executables;

    public Executable getExecutable(Update update) {
        System.out.println(UpdateType.getByUpdate(update));
        return null;
    }

}
