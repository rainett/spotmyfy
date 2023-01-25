package com.example.telegrambot.bot.repository;

import com.example.telegrambot.bot.model.AuthorizationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthorizationCodeRepository extends JpaRepository<AuthorizationCode, Long> {

}
