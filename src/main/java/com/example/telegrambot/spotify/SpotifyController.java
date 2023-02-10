package com.example.telegrambot.spotify;

import com.example.telegrambot.bot.model.AuthorizationCode;
import com.example.telegrambot.bot.repository.AuthorizationCodeRepository;
import com.rainett.javagram.config.BotConfig;
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
    private final AuthorizationCodeRepository authorizationCodeRepository;

    @GetMapping
    public void getCode(HttpServletResponse response, @RequestParam String code) throws IOException {
        AuthorizationCode authorizationCode = new AuthorizationCode();
        authorizationCode.setCode(code);
        authorizationCodeRepository.save(authorizationCode);
        String redirectUri = getRedirectUri(authorizationCode);
        response.sendRedirect(redirectUri);
    }

    private String getRedirectUri(AuthorizationCode authorizationCode) {
        String botUsername = botConfig.getUsername().substring(1);
        Long codeId = authorizationCode.getId();
        return String.format("https://t.me/%s?start=%d", botUsername, codeId);
    }

}
