package com.example.telegrambot.telegram.executable;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
public abstract class Executable {

    protected abstract List<PartialBotApiMethod<?>> run(Update update);

    public List<PartialBotApiMethod<?>> execute(Update update) {
        Long startTime = System.currentTimeMillis();
        log.info("Entered {}", this.getClass().getName());
        List<PartialBotApiMethod<?>> actions = run(update);
        Long finishTime = System.currentTimeMillis();
        log.info("Finished {}, elapsed {}", this.getClass().getName(), finishTime-startTime);
        return actions;
    }
}
