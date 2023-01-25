package com.example.telegrambot.bot.repository;

import com.example.telegrambot.bot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
