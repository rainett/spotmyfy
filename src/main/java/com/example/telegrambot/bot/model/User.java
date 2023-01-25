package com.example.telegrambot.bot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private Long id;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime lastRefreshed;

    public boolean tokenIsValid() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeOfExpiration = lastRefreshed.plusHours(1);
        return now.isBefore(timeOfExpiration);
    }
}
