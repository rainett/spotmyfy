package com.example.telegrambot.telegram.controller.executables.container;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class BotMethods {

    private final List<PartialBotApiMethod<?>> methods = new ArrayList<>();

    public void addMethod(PartialBotApiMethod<?> method) {
        methods.add(method);
    }

    public static BotMethods of(PartialBotApiMethod<?>... methods) {
        BotMethods botMethods = new BotMethods();
        Arrays.stream(methods).forEach(botMethods::addMethod);
        return botMethods;
    }

}
