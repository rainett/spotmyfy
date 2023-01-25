package com.example.telegrambot.spotify.config;

import com.example.telegrambot.bot.model.User;
import com.example.telegrambot.bot.repository.UserRepository;
import com.example.telegrambot.spotify.utils.SpotifyApiFactory;
import com.example.telegrambot.spotify.annotations.TokenRefresh;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.SuperMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.api.objects.Update;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TokenRefreshingBeanPostProcessor implements BeanPostProcessor {

    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;
    private final SpotifyConfig spotifyConfig;
    private final SpotifyApiFactory spotifyApiFactory;

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
            Annotation[] declaredAnnotations = Arrays.stream(toProxy.getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(TokenRefresh.class))
                    .findFirst()
                    .orElseThrow()
                    .getDeclaredAnnotations();

            Constructor<?> constructor = Arrays.stream(new ByteBuddy()
                    .subclass(toProxy)
                    .annotateType(toProxy.getAnnotations())
                    .method(ElementMatchers.isAnnotatedWith(TokenRefresh.class))
                    .intercept(MethodDelegation.to(new Interceptor())
                            .andThen(SuperMethodCall.INSTANCE))
                    .annotateMethod(declaredAnnotations)
                    .make()
                    .load(this.getClass().getClassLoader())
                    .getLoaded()
                    .getDeclaredConstructors())
                    .max(Comparator.comparingInt(Constructor::getParameterCount))
                    .orElseThrow();
            Parameter[] parameters = constructor.getParameters();
            Object[] args = new Object[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                args[i] = applicationContext.getBean(parameters[i].getType());
            }
            return constructor.newInstance(args);
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
        @SuppressWarnings("unused")
        public void refreshToken(Update update) {
            Long userId = getUserId(update);
            assert userId != null;
            Optional<User> userCodeOptional = userRepository.findById(userId);
            if (userCodeOptional.isEmpty() || userCodeOptional.get().getRefreshToken() == null) {
                return;
            }
            User user = userCodeOptional.get();
            if (user.tokenIsValid()) {
                return;
            }
            log.info("Refreshing user's token");
            SpotifyApi spotifyApi = spotifyApiFactory
                    .getSpotifyApiFromRefreshToken(spotifyConfig, user.getRefreshToken());
            AuthorizationCodeRefreshRequest codeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();
            try {
                AuthorizationCodeCredentials codeCredentials = codeRefreshRequest.execute();
                user.setAccessToken(codeCredentials.getAccessToken());
                user.setLastRefreshed(LocalDateTime.now());
                userRepository.save(user);
            } catch (IOException | SpotifyWebApiException | ParseException e) {
                log.error("Error during token refresh", e);
            }
        }
    }
}
