package com.example.telegrambot.bot.commands;

import com.example.telegrambot.bot.service.toptracks.TopTracksService;
import com.rainett.javagram.annotations.Command;
import com.rainett.javagram.annotations.Run;
import com.rainett.javagram.controller.executor.BotExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequiredArgsConstructor
@Slf4j
@Command(value = "/top_tracks")
public class TopTracksCommand {

    private final TopTracksService topTracksService;
    private final BotExecutor bot;

    @Run
    public void run(Update update) {
        Message message = update.getMessage();
        SendMessage topTracksMenu = topTracksService.prepareMenu(message);
        bot.execute(topTracksMenu);
    }

}
