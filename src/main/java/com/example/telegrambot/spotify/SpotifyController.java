package com.example.telegrambot.spotify;

import com.example.telegrambot.spotify.model.UserCode;
import com.example.telegrambot.spotify.repository.UserCodeRepository;
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
    private final UserCodeRepository userCodeRepository;

    @GetMapping
    public void getCode(HttpServletResponse response, @RequestParam String code) throws IOException {
        UserCode userCode = new UserCode();
        userCode.setCode(code);
        userCodeRepository.save(userCode);
        response.sendRedirect("https://t.me/" + botConfig.getUsername().substring(1) +
                "?start=" + userCode.getId());
    }

}
