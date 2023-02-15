package com.example.telegrambot.bot.repository;

import com.example.telegrambot.bot.model.User;
import com.example.telegrambot.spotify.exceptions.UserNotFoundException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    default User getByUserId(Long userId) throws UserNotFoundException {
        return findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with id = [" + userId + "] was not found"));
    }

}
