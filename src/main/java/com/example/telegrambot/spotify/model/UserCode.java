package com.example.telegrambot.spotify.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_codes",
        uniqueConstraints = {
                @UniqueConstraint(name = "UQ_user_code_user_id", columnNames = "user_id")
        }
)
public class UserCode {

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
        UserCode userCode = (UserCode) o;
        return id != null && Objects.equals(id, userCode.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public boolean tokenIsValid() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minusHours = now.minusHours(1);
        return minusHours.isAfter(lastRefreshed);
    }
}
