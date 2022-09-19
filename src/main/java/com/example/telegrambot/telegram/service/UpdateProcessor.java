package com.example.telegrambot.telegram.service;

import com.example.telegrambot.telegram.exceptions.ExecutableNotFoundException;
import com.example.telegrambot.telegram.exceptions.NotAnExecutableException;
import com.example.telegrambot.telegram.executable.Executable;
import com.example.telegrambot.telegram.executable.ExecutablesContainer;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class UpdateProcessor {

    public List<PartialBotApiMethod<?>>
    process(Update update, ExecutablesContainer executables)
            throws ExecutableNotFoundException, NotAnExecutableException {
        Executable executable = executables.getExecutable(update);
        return executable.execute(update);
    }

}
