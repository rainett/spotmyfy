package com.example.telegrambot.spotify.repository;

import com.example.telegrambot.spotify.model.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {

    Optional<UserCode> getByUserId(Long userId);

    default UserCode getFromRefreshToken(AuthorizationCodeCredentials authorizationCodeRequest, Long userId) {
        Optional<String> accessToken = Optional.ofNullable(authorizationCodeRequest.getAccessToken());
        Optional<String> refreshToken = Optional.ofNullable(authorizationCodeRequest.getRefreshToken());
        UserCode userCode = getByUserId(userId).orElse(new UserCode());
        userCode.setUserId(userId);
        accessToken.ifPresent(userCode::setAccessToken);
        refreshToken.ifPresent(t -> {
            userCode.setRefreshToken(t);
            userCode.setLastRefreshed(LocalDateTime.now());
        });
        return userCode;
    }

}
