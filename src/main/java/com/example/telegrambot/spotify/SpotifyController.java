package com.example.telegrambot.spotify;

import com.example.telegrambot.bot.model.User;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.telegram.config.BotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/spotify")
public class SpotifyController {

    private final BotConfig botConfig;
    private final UserRepository userRepository;

    @GetMapping
    public void getCode(HttpServletResponse response, @RequestParam String code) throws IOException {
        User user = new User();
        user.setCode(code);
        userRepository.save(user);
        response.sendRedirect("https://t.me/" + botConfig.getUsername().substring(1) +
                "?start=" + user.getId());
    }

}
