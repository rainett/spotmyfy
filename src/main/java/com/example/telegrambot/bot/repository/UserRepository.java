package com.example.telegrambot.bot.repository;

import com.example.telegrambot.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> getByUserId(Long userId);

    default User getFromCredentials(AuthorizationCodeCredentials authorizationCodeRequest, Long userId) {
        Optional<String> accessToken = Optional.ofNullable(authorizationCodeRequest.getAccessToken());
        Optional<String> refreshToken = Optional.ofNullable(authorizationCodeRequest.getRefreshToken());
        User user = getByUserId(userId).orElse(new User());
        user.setUserId(userId);
        accessToken.ifPresent(t -> {
            user.setAccessToken(t);
            user.setLastRefreshed(LocalDateTime.now());
        });
        refreshToken.ifPresent(user::setRefreshToken);
        return user;
    }

}
