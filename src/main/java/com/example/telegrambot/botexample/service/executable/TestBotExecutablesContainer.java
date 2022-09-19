package com.example.telegrambot.botexample.service.executable;

import com.example.telegrambot.telegram.executable.ExecutablesContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TestBotExecutablesContainer extends ExecutablesContainer {

    @Autowired
    public TestBotExecutablesContainer(List<TestBotExecutable> executables) {
        this.executables = executables;
    }

}
