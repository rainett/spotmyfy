package com.example.telegrambot.spotify.config;

import com.example.telegrambot.spotify.annotations.TokenRefresh;
import com.example.telegrambot.spotify.model.UserCode;
import com.example.telegrambot.spotify.repository.UserCodeRepository;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.telegram.config.SpotifyConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;

import org.apache.hc.core5.http.ParseException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import org.telegram.telegrambots.meta.api.objects.Update;

import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TokenRefreshingBeanPostProcessor implements BeanPostProcessor {

    private final UserCodeRepository userCodeRepository;
    private final SpotifyConfig spotifyConfig;

    @Override
    public Object postProcessAfterInitialization(@Nonnull Object bean, @Nonnull String beanName) throws BeansException {
        if (!isMarked(bean)) {
            return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
        Class<?> toProxy = bean.getClass();
        return proxyBean(bean, toProxy);
    }

    private Object proxyBean(Object bean, Class<?> toProxy) {
        try {
            return new ByteBuddy()
                    .subclass(toProxy)
                    .annotateType(toProxy.getAnnotations())
                    .method(ElementMatchers.isAnnotatedWith(TokenRefresh.class))
                    .intercept(MethodDelegation.to(new Interceptor())
                            .andThen(SuperMethodCall.INSTANCE))
                    .annotateMethod(toProxy.getDeclaredMethod("run", Update.class).getDeclaredAnnotations())
                    .make()
                    .load(this.getClass().getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructor(UserCodeRepository.class)
                    .newInstance(userCodeRepository);
        } catch (Exception e) {
            log.error("Error searching for a method", e);
            return bean;
        }
    }

    private Long getUserId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getId();
        }
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        return null;
    }

    private boolean isMarked(Object bean) {
        return Arrays.stream(bean.getClass().getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(TokenRefresh.class));
    }


    public class Interceptor {
        public void refreshToken(Update update) {
            Long userId = getUserId(update);
            Optional<UserCode> userCodeOptional = userCodeRepository.getByUserId(userId);
            if (userCodeOptional.isEmpty() || userCodeOptional.get().getRefreshToken() == null) {
                return;
            }
            UserCode userCode = userCodeOptional.get();
            if (userCode.tokenIsValid()) {
                return;
            }
            log.info("Refreshing user's token");
            SpotifyApi spotifyApi = SpotifyApiFactory
                    .getSpotifyApiFromRefreshToken(spotifyConfig, userCode.getRefreshToken());
            AuthorizationCodeRefreshRequest codeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
            try {
                AuthorizationCodeCredentials codeCredentials = codeRefreshRequest.execute();
                userCode = userCodeRepository.getFromRefreshToken(codeCredentials, userId);
                userCodeRepository.save(userCode);
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                log.error("Error during token refresh", e);
            }
        }
    }
}
