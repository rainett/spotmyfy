package com.example.telegrambot.spotify.repository;

import com.example.telegrambot.spotify.model.UserCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCodeRepository extends JpaRepository<UserCode, Long> {

    Optional<UserCode> getByUserId(Long userId);

}
