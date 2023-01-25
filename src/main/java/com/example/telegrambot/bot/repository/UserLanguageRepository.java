package com.example.telegrambot.bot.repository;

import com.example.telegrambot.bot.model.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.function.Supplier;

public interface UserLanguageRepository extends JpaRepository<UserLanguage, Long> {

    default UserLanguage findByUserId(Long userId) {
        UserLanguage other = new UserLanguage();
        other.setLocaleCode("en_US");
        other.setUserId(userId);
        return findById(userId).orElse(other);
    }

}
