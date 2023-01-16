package com.example.telegrambot.bot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_user_code_user_id", columnNames = "user_id")
        }
)
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String code;

    @Column(name = "user_id")
    private Long userId;

    private String accessToken;

    private String refreshToken;

    private LocalDateTime lastRefreshed;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean tokenIsValid() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime timeOfExpiration = lastRefreshed.plusHours(1);
        return now.isBefore(timeOfExpiration);
    }
}
