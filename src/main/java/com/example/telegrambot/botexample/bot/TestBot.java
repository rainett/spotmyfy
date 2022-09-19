package com.example.telegrambot.botexample.bot;

import com.example.telegrambot.botexample.service.executable.TestBotExecutablesContainer;
import com.example.telegrambot.telegram.Bot;
import com.example.telegrambot.telegram.service.PropertyParser;
import com.example.telegrambot.telegram.service.UpdateProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestBot extends Bot {

    @Autowired
    public TestBot(TestBotExecutablesContainer executables, UpdateProcessor updateProcessor) {
        super(executables, PropertyParser.getProperty("bot.name"),
                PropertyParser.getProperty("bot.token"), updateProcessor);
    }

}
